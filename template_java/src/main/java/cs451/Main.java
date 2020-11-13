package cs451;

import java.io.*;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

public class Main {

    private static String outputPath;
    public static Queue<String> outputBuffer = new LinkedList<>();

    private static void handleSignal() {
        //immediately stop network packet processing
        System.out.println("Immediately stopping network packet processing.");

        //write/flush output file if necessary
        System.out.println("Writing output.");

        try {
            File outputFile = new File(outputPath);
            FileOutputStream s = new FileOutputStream(outputFile);
            OutputStreamWriter w = new OutputStreamWriter(s);
            while (outputBuffer.peek() != null) {
                w.write(outputBuffer.poll());
                w.write("\n");
            }
            w.close();
            s.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void initSignalHandlers() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                handleSignal();
            }
        });
    }

    public static void main(String[] args) throws InterruptedException {
        Parser parser = new Parser(args);
        parser.parse();

        initSignalHandlers();

        // example
        long pid = ProcessHandle.current().pid();
        System.out.println("My PID is " + pid + ".");
        System.out.println("Use 'kill -SIGINT " + pid + " ' or 'kill -SIGTERM " + pid + " ' to stop processing packets.");

        int myId = parser.myId();
        System.out.println("My id is " + myId + ".");

        Host myHost = null;
        System.out.println("List of hosts is:");
        for (Host host: parser.hosts()) {
            System.out.println(host.getId() + ", " + host.getIp() + ", " + host.getPort());
            if (host.getId()==myId && myHost==null) {
                myHost = host;
            }
        }

        int nbMessages = 0;

        if (parser.hasConfig()) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(new File(parser.config())));
                String s = reader.readLine();
                nbMessages = Integer.parseInt(s);
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (myHost != null) {
            myHost.init(parser.hosts(), nbMessages);
        } else {
            System.out.println("Invalid id");
        }

        System.out.println("Barrier: " + parser.barrierIp() + ":" + parser.barrierPort());
        System.out.println("Signal: " + parser.signalIp() + ":" + parser.signalPort());
        System.out.println("Output: " + parser.output());
        outputPath = parser.output();
        // if config is defined; always check before parser.config()
        if (parser.hasConfig()) {
            System.out.println("Config: " + parser.config());
        }


        Coordinator coordinator = new Coordinator(parser.myId(), parser.barrierIp(), parser.barrierPort(), parser.signalIp(), parser.signalPort());

	System.out.println("Waiting for all processes for finish initialization");
        coordinator.waitOnBarrier();

	System.out.println("Broadcasting messages...");

	if (myHost != null) myHost.broadcast();

	System.out.println("Signaling end of broadcasting messages");
        coordinator.finishedBroadcasting();

	while (true) {
	    // Sleep for 1 hour
	    Thread.sleep(60 * 60 * 1000);
	}
    }
}

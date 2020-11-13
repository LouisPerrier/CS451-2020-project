package cs451;

import cs451.protocol.FairlossLink;
import cs451.protocol.PerfectLink;

import java.net.InetAddress;
import java.net.UnknownHostException;


public class Host {

    private static final String IP_START_REGEX = "/";

    private int id;
    private String ip;
    private int port = -1;

    private ReceiveThread receiveThread;
    private PerfectLink perfectLink; //tmp

    public boolean populate(String idString, String ipString, String portString) {
        try {
            id = Integer.parseInt(idString);

            String ipTest = InetAddress.getByName(ipString).toString();
            if (ipTest.startsWith(IP_START_REGEX)) {
                ip = ipTest.substring(1);
            } else {
                ip = InetAddress.getByName(ipTest.split(IP_START_REGEX)[0]).getHostAddress();
            }

            port = Integer.parseInt(portString);
            if (port <= 0) {
                System.err.println("Port in the hosts file must be a positive number!");
                return false;
            }
        } catch (NumberFormatException e) {
            if (port == -1) {
                System.err.println("Id in the hosts file must be a number!");
            } else {
                System.err.println("Port in the hosts file must be a number!");
            }
            return false;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        FairlossLink fairlossLink = new FairlossLink(ip, port);
        perfectLink = new PerfectLink(fairlossLink);
        fairlossLink.addListener(perfectLink);

        receiveThread = new ReceiveThread(fairlossLink);

        return true;
    }

    public int getId() {
        return id;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public void broadcast() throws InterruptedException {
        receiveThread.start();
        Thread.sleep(5000);
        if (id == 1)
            perfectLink.send(1, "localhost", 11002);
        else
            perfectLink.send(1, "localhost", 11001);
    }

}

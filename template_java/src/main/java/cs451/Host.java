package cs451;

import cs451.protocol.BestEffortBroadcast;
import cs451.protocol.FairLossLink;
import cs451.protocol.PerfectLink;
import cs451.protocol.UniformReliableBroadcast;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;


public class Host {

    private static final String IP_START_REGEX = "/";

    private int id;
    private String ip;
    private int port = -1;

    private ReceiveThread receiveThread;
    //private PerfectLink perfectLink;
    private UniformReliableBroadcast urb;

    private int nbMessages;

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

        return true;
    }

    public void init(List<Host> hosts, int nbMessages) {
	    FairLossLink fairlossLink = new FairLossLink(ip, port, hosts);
        PerfectLink perfectLink = new PerfectLink(fairlossLink, hosts);
        BestEffortBroadcast beb = new BestEffortBroadcast(perfectLink, hosts);
        urb = new UniformReliableBroadcast(beb, hosts.size());

        receiveThread = new ReceiveThread(fairlossLink);

        this.nbMessages = nbMessages;
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

        /*if (id == 1) {
            perfectLink.send(new Message(1, id), "localhost", 11002);
        }
        else if (id == 2) {
            perfectLink.send(new Message(1, id), "localhost", 11001);
            perfectLink.send(new Message(2, id), "localhost", 11001);
            perfectLink.send(new Message(3, id), "localhost", 11001);
        }
        else if (id == 3) {
            perfectLink.send(new Message(1, id), "localhost", 11002);
        }*/

        for (int i =1 ; i<=nbMessages ; i++) {
            urb.broadcast(new Message(i, id));
        }
    }

}

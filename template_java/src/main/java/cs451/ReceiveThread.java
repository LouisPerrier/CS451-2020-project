package cs451;

import cs451.protocol.FairlossLink;

import java.util.List;

public class ReceiveThread extends Thread {

    private FairlossLink fairlossLink;
    private List<Host> hosts;

    public ReceiveThread(FairlossLink fairlossLink, List<Host> hosts) {
        this.fairlossLink = fairlossLink;
        this.hosts = hosts;
    }

    @Override
    public void run() {
        while (true) {
            fairlossLink.receive(hosts);
        }
    }
}

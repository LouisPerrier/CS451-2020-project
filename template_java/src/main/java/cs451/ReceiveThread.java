package cs451;

import cs451.protocol.FairlossLink;

public class ReceiveThread extends Thread {

    private FairlossLink fairlossLink;

    public ReceiveThread(FairlossLink fairlossLink) {
        this.fairlossLink = fairlossLink;
    }

    @Override
    public void run() {
        while (true) {
            int seq = fairlossLink.receive();
            System.out.println("message received : " + seq);
        }
    }
}

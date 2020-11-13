package cs451.protocol;

import cs451.DeliveredMessage;

import java.util.HashSet;
import java.util.Set;

public class PerfectLink implements Listener, Sender {

    private Listener listener;
    private FairlossLink fairlossLink;

    private Set<DeliveredMessage> delivered;

    public PerfectLink(FairlossLink fairlossLink){
        this.fairlossLink = fairlossLink;
        fairlossLink.addListener(this);

        delivered = new HashSet<>();
    }

    @Override
    public void send(Integer seq, String dstIp, int dstPort) {
        fairlossLink.send(seq, dstIp, dstPort);
    }

    @Override
    public void deliver(DeliveredMessage m, String srcIp, int srcPort) {
        if (!delivered.contains(m)) {
            delivered.add(m);
            //listener.deliver(seq, srcIp, srcPort);
            System.out.println("Message delivered : " + m.seq);
        }
    }
}

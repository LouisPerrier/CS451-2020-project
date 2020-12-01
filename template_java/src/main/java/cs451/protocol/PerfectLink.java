package cs451.protocol;

import cs451.Host;
import cs451.Message;
import cs451.MessageWithId;

import java.util.*;

public class PerfectLink extends UnderlyingProtocol implements Listener {

    private FairLossLink fairLossLink;
    private List<Host> hosts;
    private Timer timer;

    private Map<MessageWithId, AbstractMap.SimpleEntry<String, Integer>> unAcked;
    private Set<MessageWithId> delivered;

    private final long period = 1000;

    public PerfectLink(FairLossLink fairLossLink, List<Host> hosts){
        this.fairLossLink = fairLossLink;
        this.hosts = hosts;
        fairLossLink.addListener(this);

        unAcked = new HashMap<>();
        delivered = new HashSet<>();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                for (MessageWithId m : unAcked.keySet()) {
                    AbstractMap.SimpleEntry<String, Integer> e = unAcked.get(m);
                    fairLossLink.send(m, e.getKey(), e.getValue());
                }
            }
        }, 0, period);
    }

    public void send(Message m, String dstIp, int dstPort) {
        MessageWithId m1 = new MessageWithId(m, UUID.randomUUID());
        System.out.println("sending message " + m.seq + " --" + m1.uuid);
        unAcked.put(m1, new AbstractMap.SimpleEntry<>(dstIp, dstPort));
        fairLossLink.send(m1, dstIp, dstPort);
    }

    @Override
    public void deliver(MessageWithId m, int srcId) {
        System.out.println("receiving message " + m.message.seq + " from " + srcId + " -" + m.uuid);
        if (m.message.seq == -1) { //ack
            unAcked.remove(m);
        } else {
            String srcIp = "";
            int srcPort = 0;
            for (Host h : hosts) {
                if (h.getId() == srcId) {
                    srcIp = h.getIp();
                    srcPort = h.getPort();
                }
            }
            MessageWithId ack = new MessageWithId(new Message(-1, srcId), m.uuid);
            fairLossLink.send(ack, srcIp, srcPort);
            System.out.println("sending ack");

            if (!delivered.contains(m)) {
                System.out.println("delivering message");
                delivered.add(m);
                listener.deliver(m, srcId);
            }
        }
    }
}

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

    private final long period = 500;

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
        unAcked.put(m1, new AbstractMap.SimpleEntry<>(dstIp, dstPort));
        fairLossLink.send(m1, dstIp, dstPort);
    }

    @Override
    public void deliver(MessageWithId m, int srcId) {

        if (delivered.contains(m)) {
            String srcIp = "";
            int srcPort = 0;
            for (Host h : hosts) {
                if (h.getId() == srcId) {
                    srcIp = h.getIp();
                    srcPort = h.getPort();
                }
            }
            fairLossLink.send(m, srcIp, srcPort); //send ack
        } else if (unAcked.containsKey(m)) {
            unAcked.remove(m);
        } else {
            delivered.add(m);
            listener.deliver(m, srcId);
        }
    }
}

package cs451.protocol;

import cs451.Message;

import java.util.*;

public class UniformReliableBroadcast extends UnderlyingProtocol implements Listener, Broadcaster {

    private BestEffortBroadcast beb;

    private final int nHosts;

    private Set<Message> delivered, pending;
    private Map<Message, Set<Integer>> ack;

    public UniformReliableBroadcast(BestEffortBroadcast beb,  int nHosts) {
        this.beb = beb;
        beb.addListener(this);

        this.nHosts = nHosts;

        delivered = new HashSet<>();
        pending = new HashSet<>();
        ack = new HashMap<>();
    }

    @Override
    public void broadcast(Message m) {
        pending.add(m);
        beb.broadcast(m);
        checkAndDeliver(m);
    }

    @Override
    public void deliver(Message m, int srcId) {

        if (!ack.containsKey(m)) {
            ack.put(m, new HashSet<>(srcId));
        } else {
            ack.get(m).add(srcId);
        }

        if (!pending.contains(m)) {
            pending.add(m);
            beb.broadcast(m);
        }
        checkAndDeliver(m);
    }

    public void checkAndDeliver(Message m) {
        if (ack.containsKey(m) && ack.get(m).size() > nHosts/2 && !delivered.contains(m)) {
            delivered.add(m);
            listener.deliver(m, m.senderId);
        }
    }
}

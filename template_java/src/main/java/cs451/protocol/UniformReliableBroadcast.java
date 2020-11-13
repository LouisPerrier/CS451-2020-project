package cs451.protocol;

import cs451.Message;
import cs451.MessageWithId;

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
	System.out.println("broadcast called");
	//m.setUuid(UUID.randomUUID()); TODO delete
        pending.add(m);
        beb.broadcast(m);
        checkAndDeliver(m);
    }

    @Override
    public void deliver(MessageWithId m, int srcId) {
        Message message = m.message;
	//System.out.println("message : " + m.seq + ", sender : " + m.senderId + ", source : " + srcId);
        if (!ack.containsKey(message)) {
	    if (ack.size() < 10) {
	    for (Message m1 : ack.keySet()){
	        System.out.println("equals is " + m1.equals(message));
		System.out.println("m1 : seq is " + m1.seq + " and id is " + m1.senderId);
	        System.out.println("m : seq is " + message.seq + " and id is " + message.senderId );
	    }
	    }
            ack.put(message, new HashSet<>(srcId));
        } else {System.out.println("else");
            ack.get(message).add(srcId);
        }

        if (!pending.contains(message)) {//System.out.println("if !pending");
            pending.add(message);
            beb.broadcast(message);
        }
        checkAndDeliver(message);
	//System.out.println("size is " + ack.size());
    }

    private void checkAndDeliver(Message m) {
        if (ack.containsKey(m) && ack.get(m).size() > nHosts/2 && !delivered.contains(m)) {
            delivered.add(m);
            //listener.deliver(m, m.senderId); TODO uncomment
	    System.out.println("delivered message " + m.seq + "  broadcast by " + m.senderId);
        }
    }
}

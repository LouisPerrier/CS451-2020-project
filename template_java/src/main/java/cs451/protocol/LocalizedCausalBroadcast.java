package cs451.protocol;

import cs451.Main;
import cs451.Message;
import cs451.MessageWithId;

import java.util.HashSet;
import java.util.Set;

public class LocalizedCausalBroadcast implements Listener{

    private UniformReliableBroadcast urb;
    private Set<Message> pending;
    private int lsn;
    private int[] v;
    private Set<Integer> dependencies;
    private int hostId;

    public LocalizedCausalBroadcast(UniformReliableBroadcast urb, int nHosts, Set<Integer> dependencies, int hostId) {
        this.urb = urb;
        urb.addListener(this);
        this.dependencies = dependencies;
        this.hostId = hostId;
        pending = new HashSet<>();
        v = new int[nHosts];
        lsn = 0;
    }

    public void broadcast(Message m) {
        for (Integer i : dependencies) {
            m.vectorClock[i-1] = v[i-1];
        }
        m.vectorClock[hostId-1] = lsn;
        lsn++;
        urb.broadcast(m);
    }

    @Override
    public void deliver(MessageWithId m, int srcId) {
        pending.add(m.message);

        boolean more = true;
        while (more) {
            Message toRemove = null;
            for (Message p : pending) {
                boolean smaller = true;
                for (int i = 0; i < v.length; i++) {
                    if (p.vectorClock[i] > v[i]) {
                        smaller = false;
                        break;
                    }
                }
                if (smaller) {
                    toRemove = p;
                    v[p.senderId-1] = v[p.senderId-1] + 1;
                }
            }
            if (toRemove != null) {
                pending.remove(toRemove);
                Main.outputBuffer.add("d " + toRemove.senderId + " " + toRemove.seq);
            } else {
                more = false;
            }
        }
    }
}

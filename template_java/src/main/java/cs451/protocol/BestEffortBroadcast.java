package cs451.protocol;

import cs451.Message;
import cs451.Host;

import java.util.List;

public class BestEffortBroadcast extends UnderlyingProtocol implements Broadcaster, Listener{

    private PerfectLink perfectLink;
    private List<Host> hosts;

    public BestEffortBroadcast(PerfectLink perfectLink, List<Host> hosts) {
        this.perfectLink = perfectLink;
        perfectLink.addListener(this);
        this.hosts = hosts;
    }

    @Override
    public void broadcast(Message m) {
        for (Host h : hosts) {
            perfectLink.send(m, h.getIp(), h.getPort());
        }
    }

    @Override
    public void deliver(Message m, int srcId) {
	System.out.println("reached");
	m.setUuid(null); //TODO test
        listener.deliver(m, srcId);
    }
}

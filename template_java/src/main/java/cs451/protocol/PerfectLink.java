package cs451.protocol;

import cs451.Message;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PerfectLink extends UnderlyingProtocol implements Listener, Sender {

    private FairLossLink fairlossLink;

    private Set<Message> delivered;

    public PerfectLink(FairLossLink fairlossLink){
        this.fairlossLink = fairlossLink;
        fairlossLink.addListener(this);

        delivered = new HashSet<>();
    }

    @Override
    public void send(Message m, String dstIp, int dstPort) {
        //m.setUuid(UUID.randomUUID());
        fairlossLink.send(m, dstIp, dstPort);
    }

    @Override
    public void deliver(Message m, int srcId) {
        /*if (!delivered.contains(m)) {
            delivered.add(m);
            //m.setUuid(null);
            listener.deliver(m, srcId);
        }*/
	listener.deliver(m, srcId); //TODO delete
    }
}

package cs451.protocol;

import cs451.Message;
import cs451.MessageWithId;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PerfectLink extends UnderlyingProtocol implements Listener, Sender {

    private FairLossLink fairlossLink;

    private Set<MessageWithId> delivered;

    public PerfectLink(FairLossLink fairlossLink){
        this.fairlossLink = fairlossLink;
        fairlossLink.addListener(this);

        delivered = new HashSet<>();
    }

    @Override
    public void send(Message m, String dstIp, int dstPort) {
        MessageWithId m1 = new MessageWithId(m, UUID.randomUUID());
        fairlossLink.send(m1, dstIp, dstPort);
    }

    @Override
    public void deliver(MessageWithId m, int srcId) {
        if (!delivered.contains(m)) {
            delivered.add(m);
            listener.deliver(m, srcId);
        }
    }
}

package cs451;

public class DeliveredMessage {
    public int seq;
    public int senderId;

    public DeliveredMessage(int seq, int senderId) {
        this.seq = seq;
        this.senderId = senderId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (! (o instanceof DeliveredMessage)) return false;

        DeliveredMessage m = (DeliveredMessage) o;
        return this.seq == m.seq && this.senderId == m.senderId;
    }
}

package cs451;

import java.util.UUID;

public class Message {
    public int seq;
    public int senderId;
    //public UUID uuid;

    public Message(int seq, int senderId) {
        this.seq = seq;
        this.senderId = senderId;
        //this.uuid = null;
    }

    /*public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }*/

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (! (o instanceof Message)) return false;

        Message m = (Message) o;

        //if (m.uuid == null && this.uuid == null) {
            return this.seq == m.seq && this.senderId == m.senderId;
        /*} else if (m.uuid != null && this.uuid != null) {
            return this.uuid.equals(m.uuid);
	}
        else
            return false;*/
    }

    @Override
    public int hashCode() {
        //if (uuid == null)
            return seq;
        /*else
            return uuid.hashCode();*/
    }
}

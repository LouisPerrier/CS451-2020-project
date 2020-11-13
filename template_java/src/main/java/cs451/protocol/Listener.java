package cs451.protocol;

public interface Listener {
    void deliver(Integer seq, String srcIp, int srcPort);
}

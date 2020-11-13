package cs451.protocol;

public interface Sender {
    void send(Integer seq, String dstIp, int dstPort);
}

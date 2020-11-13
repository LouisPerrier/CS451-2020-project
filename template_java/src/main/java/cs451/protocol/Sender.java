package cs451.protocol;

import cs451.Message;

public interface Sender {
    void send(Message m, String dstIp, int dstPort);
}

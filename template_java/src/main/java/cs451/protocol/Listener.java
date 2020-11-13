package cs451.protocol;

import cs451.DeliveredMessage;

public interface Listener {
    void deliver(DeliveredMessage m, String srcIp, int srcPort);
}

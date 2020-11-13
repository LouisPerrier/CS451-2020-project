package cs451.protocol;

import cs451.Message;

public interface Listener {
    void deliver(Message m, int srcId);
}

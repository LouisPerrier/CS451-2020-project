package cs451.protocol;

public abstract class UnderlyingProtocol {
    protected Listener listener;

    public void addListener(Listener listener) {
        this.listener = listener;
    }
}

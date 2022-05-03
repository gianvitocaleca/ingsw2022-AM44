package it.polimi.ingsw.client;

public class PingState {
    private boolean received;

    public PingState() {
        this.received = false;
    }

    public synchronized boolean isReceived() {
        return received;
    }

    public synchronized void setReceived(boolean received) {
        this.received = received;
    }
}

package it.polimi.ingsw.network.ping;

public class PingState {
    private boolean received;
    private boolean closeConnection;

    public PingState() {
        this.received = false;
        this.closeConnection = false;
    }

    public synchronized boolean isReceived() {
        return received;
    }

    public synchronized void setReceived(boolean received) {
        this.received = received;
    }

    public synchronized boolean isCloseConnection() {
        return closeConnection;
    }

    public synchronized void setCloseConnection(boolean closeConnection) {
        this.closeConnection = closeConnection;
    }
}

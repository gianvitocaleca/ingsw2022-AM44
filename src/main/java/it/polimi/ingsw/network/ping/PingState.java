package it.polimi.ingsw.network.ping;

public class PingState {
    private boolean received;
    private boolean closeConnection;

    /**
     * Used to keep track of the connection state of a player
     */
    public PingState() {
        this.received = false;
        this.closeConnection = false;
    }

    /**
     *
     * @return whether the player has received the ping
     */
    public synchronized boolean isReceived() {
        return received;
    }

    /**
     *
     * @param received whether the player has received the ping
     */
    public synchronized void setReceived(boolean received) {
        this.received = received;
    }

    /**
     *
     * @return whether the connection has been closed
     */
    public synchronized boolean isCloseConnection() {
        return closeConnection;
    }

    /**
     *
     * @param closeConnection whether the connection has been closed
     */
    public synchronized void setCloseConnection(boolean closeConnection) {
        this.closeConnection = closeConnection;
    }
}

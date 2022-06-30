package it.polimi.ingsw.network.server;

import java.net.Socket;

/**
 * This class is used to contain information about player's connection, each socket has an associated id
 * PlayerInfo contains player's choices during the login phase.
 */
public class SocketID {
    private int id;
    private Socket socket;
    private PlayerInfo playerInfo;
    private boolean isConnected;

    private boolean needsReplacement;

    /**
     * Used to keep the player's info in one object
     * @param id uniquely identifies the player
     * @param socket is the connection socket
     */
    public SocketID(int id, Socket socket) {
        this.id = id;
        this.socket = socket;
        this.isConnected = true;
        this.playerInfo = new PlayerInfo();
        this.needsReplacement = true;
    }

    /**
     *
     * @return is the identifier
     */
    public int getId() {
        return id;
    }

    /**
     *
     * @return is the connection socket
     */
    public Socket getSocket() {
        return socket;
    }

    /**
     *
     * @return is the player's basic info
     */
    public PlayerInfo getPlayerInfo() {
        return playerInfo;
    }

    /**
     * Used to set the player's info
     * @param playerInfo is the given infos
     */
    public void setPlayerInfo(PlayerInfo playerInfo) {
        this.playerInfo = playerInfo;
    }

    /**
     *
     * @return whether the player is still connected
     */
    public boolean isConnected() {
        return isConnected;
    }

    /**
     * Used to set the connection status of the player
     * @param connected is the given status
     */
    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    /**
     * Used for reconnection purposes
     * @return whether the player needs to be replaced
     * @throws InterruptedException
     */
    public boolean isNeedsReplacement() throws InterruptedException {
        synchronized (this) {
            while (!needsReplacement) {
                this.wait();
            }
        }
        return true;
    }

    /**
     * Used to set the replacement status of the player
     * @param needsReplacement is the given status
     */
    public void setNeedsReplacement(boolean needsReplacement) {
        synchronized (this) {
            this.needsReplacement = needsReplacement;
            notifyAll();
        }
    }

}

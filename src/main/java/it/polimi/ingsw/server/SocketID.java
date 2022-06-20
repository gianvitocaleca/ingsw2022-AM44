package it.polimi.ingsw.server;

import java.net.Socket;

public class SocketID {
    private int id;
    private Socket socket;
    private PlayerInfo playerInfo;
    private boolean isConnected;

    private boolean needsReplacement;

    public SocketID(int id, Socket socket) {
        this.id = id;
        this.socket = socket;
        this.isConnected = true;
        this.playerInfo = new PlayerInfo();
        this.needsReplacement = true;
    }

    public int getId() {
        return id;
    }

    public Socket getSocket() {
        return socket;
    }

    public PlayerInfo getPlayerInfo() {
        return playerInfo;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public void setSocket(Socket socket){
        this.socket = socket;
    }

    public boolean isNeedsReplacement() throws InterruptedException {
        synchronized (this){
            while(!needsReplacement){
                this.wait();
            }
        }
        return true;
    }

    public void setNeedsReplacement(boolean needsReplacement) {
        synchronized (this){
            this.needsReplacement = needsReplacement;
            notifyAll();
        }
    }

    public void setId(int id) {
        this.id = id;
    }
}

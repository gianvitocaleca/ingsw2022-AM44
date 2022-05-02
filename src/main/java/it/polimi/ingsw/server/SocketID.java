package it.polimi.ingsw.server;

import java.net.Socket;

public class SocketID {
    int id;
    Socket socket;
    private PlayerInfo playerInfo;
    boolean isConnected;

    public SocketID(int id, Socket socket) {
        this.id = id;
        this.socket = socket;
        this.isConnected = true;
        playerInfo = new PlayerInfo();
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

    public void setPlayerInfo(PlayerInfo playerInfo) {
        this.playerInfo = playerInfo;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

}

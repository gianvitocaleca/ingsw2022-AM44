package it.polimi.ingsw.server.controller.events;

import java.net.Socket;

public class CloseConnectionEvent {
    private Socket socket;

    public CloseConnectionEvent(Socket socket) {
        this.socket = socket;
    }

    public Socket getSocket() {
        return socket;
    }
}

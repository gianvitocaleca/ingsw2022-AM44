package it.polimi.ingsw.server.controller.events;

import java.net.Socket;
import java.util.EventObject;

public class CloseConnectionEvent extends EventObject {
    private Socket socket;

    public CloseConnectionEvent(Object source, Socket socket) {
        super(source);
        this.socket = socket;
    }

    public Socket getSocket() {
        return socket;
    }
}

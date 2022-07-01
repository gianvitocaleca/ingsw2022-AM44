package it.polimi.ingsw.controller.events;

import java.net.Socket;
import java.util.EventObject;

/**
 * This event contains the socket of the rejected player.
 */
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

package it.polimi.ingsw.server.controller.events;

import java.net.Socket;
import java.util.EventObject;

public class CreationEvent extends EventObject {

    private int value;
    private Socket socket;
    public CreationEvent(Object source, int value, Socket socket) {
        super(source);
        this.value = value;
        this.socket = socket;
    }

    public int getValue() {
        return value;
    }

    public Socket getSocket() {
        return socket;
    }
}

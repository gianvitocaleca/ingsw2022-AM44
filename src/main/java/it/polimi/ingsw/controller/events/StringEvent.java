package it.polimi.ingsw.controller.events;

import it.polimi.ingsw.network.server.networkMessages.Headers;

import java.net.Socket;
import java.util.EventObject;

public class StringEvent extends EventObject {

    private Headers header;
    private String message;
    private Socket socket;

    /**
     * Constructs a prototypical Event.
     *
     * @param source the object on which the Event initially occurred
     * @throws IllegalArgumentException if source is null
     */
    public StringEvent(Object source, String message, Headers header, Socket socket) {
        super(source);
        this.message = message;
        this.header = header;
        this.socket = socket;
    }

    public String getMessage() {
        return message;
    }

    public Headers getHeader() {
        return header;
    }

    public Socket getSocket() {
        return socket;
    }

}

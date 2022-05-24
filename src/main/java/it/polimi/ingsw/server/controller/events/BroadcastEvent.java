package it.polimi.ingsw.server.controller.events;

import it.polimi.ingsw.server.networkMessages.Headers;

import java.net.Socket;
import java.util.EventObject;

public class BroadcastEvent extends EventObject {
    private Headers header;
    private String message;
    /**
     * Constructs a prototypical Event.
     *
     * @param source the object on which the Event initially occurred
     * @throws IllegalArgumentException if source is null
     */
    public BroadcastEvent(Object source, String message, Headers header) {
        super(source);
        this.message = message;
        this.header = header;
    }

    public String getMessage() {
        return message;
    }

    public Headers getHeader() {
        return header;
    }

}

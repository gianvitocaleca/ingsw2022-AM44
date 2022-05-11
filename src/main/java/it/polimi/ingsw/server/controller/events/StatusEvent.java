package it.polimi.ingsw.server.controller.events;

import it.polimi.ingsw.server.networkMessages.Headers;

import java.net.Socket;
import java.util.EventObject;

public class StatusEvent extends EventObject {

    private Headers header;

    /**
     * Constructs a prototypical Event.
     *
     * @param source the object on which the Event initially occurred
     * @throws IllegalArgumentException if source is null
     */
    public StatusEvent(Object source, Headers header) {
        super(source);
        this.header = header;
    }

    public Headers getHeader() {
        return header;
    }
}

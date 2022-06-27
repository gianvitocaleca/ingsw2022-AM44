package it.polimi.ingsw.controller.events;

import it.polimi.ingsw.network.server.networkMessages.Headers;

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

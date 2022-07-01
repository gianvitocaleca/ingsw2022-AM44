package it.polimi.ingsw.controller.events;

import it.polimi.ingsw.network.server.networkMessages.Headers;

import java.util.EventObject;

/**
 * This event contains information to send the phase message.
 */
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

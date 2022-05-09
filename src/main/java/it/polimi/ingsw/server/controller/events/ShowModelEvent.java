package it.polimi.ingsw.server.controller.events;

import it.polimi.ingsw.server.networkMessages.Payload;

import java.util.EventObject;

public class ShowModelEvent extends EventObject {

    Payload payload;
    /**
     * Constructs a prototypical Event.
     *
     * @param source the object on which the Event initially occurred
     * @throws IllegalArgumentException if source is null
     */
    public ShowModelEvent(Object source, Payload payload) {
        super(source);
        this.payload=payload;
    }

    public Payload getPayload() {
        return payload;
    }
}

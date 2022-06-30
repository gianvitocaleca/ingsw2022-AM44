package it.polimi.ingsw.controller.events;

import it.polimi.ingsw.network.server.networkMessages.payloads.Payload;

import java.util.EventObject;

public class ShowModelEvent extends EventObject {

    private Payload payload;

    /**
     * Constructs a prototypical Event.
     *
     * @param source the object on which the Event initially occurred
     * @throws IllegalArgumentException if source is null
     */
    public ShowModelEvent(Object source, Payload payload) {
        super(source);
        this.payload = payload;
    }

    public Payload getPayload() {
        return payload;
    }
}

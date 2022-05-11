package it.polimi.ingsw.server.controller.events;

import it.polimi.ingsw.server.networkMessages.CharactersParametersPayload;

import java.util.EventObject;

public class CharacterParametersEvent extends EventObject {

    private CharactersParametersPayload parameters;

    /**
     * Constructs a prototypical Event.
     *
     * @param source the object on which the Event initially occurred
     * @throws IllegalArgumentException if source is null
     */
    public CharacterParametersEvent(Object source, CharactersParametersPayload parameters) {
        super(source);
        this.parameters = parameters;
    }

    public CharactersParametersPayload getParameters() {
        return parameters;
    }
}
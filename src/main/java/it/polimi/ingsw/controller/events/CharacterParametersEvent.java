package it.polimi.ingsw.controller.events;

import it.polimi.ingsw.network.server.networkMessages.payloads.CharactersParametersPayload;

import java.util.EventObject;

/**
 * This event contains the payload of the message from client to server,
 * that contains the player's choice.
 */
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

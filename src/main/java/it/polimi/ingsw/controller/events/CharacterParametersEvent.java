package it.polimi.ingsw.controller.events;

import it.polimi.ingsw.messages.CharactersParameters;

import java.util.EventObject;

public class CharacterParametersEvent extends EventObject {

    private CharactersParameters parameters;

    /**
     * Constructs a prototypical Event.
     *
     * @param source the object on which the Event initially occurred
     * @throws IllegalArgumentException if source is null
     */
    public CharacterParametersEvent(Object source, CharactersParameters parameters) {
        super(source);
        this.parameters = parameters;
    }

    public CharactersParameters getParameters() {
        return parameters;
    }
}

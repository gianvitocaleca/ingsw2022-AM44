package it.polimi.ingsw.server.controller.events;

import it.polimi.ingsw.server.model.enums.Name;

import java.util.EventObject;

public class CharacterPlayedEvent extends EventObject {

    private Name charactersName;

    /**
     * Constructs a prototypical Event.
     *
     * @param source the object on which the Event initially occurred
     * @throws IllegalArgumentException if source is null
     */
    public CharacterPlayedEvent(Object source, Name charactersName) {
        super(source);
        this.charactersName = charactersName;
    }

    public Name getCharactersName() {
        return charactersName;
    }
}

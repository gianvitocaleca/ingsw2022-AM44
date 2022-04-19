package it.polimi.ingsw.controller.events;

import java.util.EventObject;

public class PlayCharacterEvent extends EventObject {

    private int indexOfCharacter;
    /**
     * Constructs a prototypical Event.
     *
     * @param source the object on which the Event initially occurred
     * @throws IllegalArgumentException if source is null
     */
    public PlayCharacterEvent(Object source, int indexOfCharacter) {
        super(source);
        this.indexOfCharacter = indexOfCharacter;
    }

    public int getIndexOfCharacter() {
        return indexOfCharacter;
    }
}

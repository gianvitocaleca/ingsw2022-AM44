package it.polimi.ingsw.controller.events;

import java.util.EventObject;

/**
 * This event contains the index of the assistant played by the player
 */
public class PlanningEvent extends EventObject {

    private int indexOfAssistant;

    /**
     * Constructs a prototypical Event.
     *
     * @param source the object on which the Event initially occurred
     * @throws IllegalArgumentException if source is null
     */
    public PlanningEvent(Object source, int indexOfAssistant) {
        super(source);
        this.indexOfAssistant = indexOfAssistant;
    }

    public int getIndexOfAssistant() {
        return indexOfAssistant;
    }
}

package it.polimi.ingsw.server.controller.events;

import it.polimi.ingsw.server.model.enums.Creature;

import java.util.EventObject;
import java.util.List;

public class MoveStudentsEvent extends EventObject {
    private boolean isDestinationIsland;
    private int indexOfIsland;
    private List<Creature> creatureList;


    /**
     * Constructs a prototypical Event.
     *
     * @param source the object on which the Event initially occurred
     * @throws IllegalArgumentException if source is null
     */
    public MoveStudentsEvent(Object source, boolean isDestinationIsland, int indexOfIsland, List<Creature> creatureList) {
        super(source);
        this.isDestinationIsland = isDestinationIsland;
        this.indexOfIsland = indexOfIsland;
        this.creatureList = creatureList;
    }

    public boolean isDestinationIsland() {
        return isDestinationIsland;
    }

    public int getIndexOfIsland() {
        return indexOfIsland;
    }

    public List<Creature> getCreatureList() {
        return creatureList;
    }
}

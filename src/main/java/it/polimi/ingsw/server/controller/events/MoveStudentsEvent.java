package it.polimi.ingsw.server.controller.events;

import it.polimi.ingsw.server.model.enums.Creature;

import java.util.EventObject;
import java.util.List;

public class MoveStudentsEvent extends EventObject {

    private String username;
    private boolean isDestinationIsland;
    private int indexOfIsland;
    private List<Creature> creatureList;



    /**
     * Constructs a prototypical Event.
     *
     * @param source the object on which the Event initially occurred
     * @throws IllegalArgumentException if source is null
     */
    public MoveStudentsEvent(Object source, String username, boolean isDestinationIsland, int indexOfIsland, List<Creature> creatureList) {
        super(source);
        this.username = username;
        this.isDestinationIsland = isDestinationIsland;
        this.indexOfIsland = indexOfIsland;
        this.creatureList = creatureList;
    }

    public String getUsername() {
        return username;
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

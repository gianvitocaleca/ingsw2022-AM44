package it.polimi.ingsw.model.player;

import it.polimi.ingsw.model.enums.Creature;

public class Professor {
    private Creature creature;

    /**
     * Is the professor pawn.
     * @param creature is the type of professor
     */
    public Professor(Creature creature){
        this.creature=creature;
    }

    /**
     *
     * @return is the type of professor
     */
    public Creature getCreature() {
        return creature;
    }

    /**
     * Used for test purposes.
     * @return
     */
    @Override
    public String toString() {
        return "" + creature;

    }
}

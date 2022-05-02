package it.polimi.ingsw.server.model.player;

import it.polimi.ingsw.server.model.enums.Creature;

public class Professor {
    private Creature creature;

    public Professor(Creature creature){
        this.creature=creature;
    }

    public Creature getCreature() {
        return creature;
    }
}

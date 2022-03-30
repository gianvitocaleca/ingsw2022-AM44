package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.model.enums.Creature;
import it.polimi.ingsw.model.enums.Name;
import it.polimi.ingsw.model.Playable;

public class Thief implements Character {
    private final Name name;
    private final Playable model;

    public Thief(Name name, Playable model) {
        this.name=name;
        this.model=model;
    }

    @Override
    public void effect() {
        //Thief will ask the controller for a creature to remove from the Dining Rooms
        model.thiefEffect(Creature.BLUE_UNICORNS); //PLACEHOLDER CREATURE
    }


    @Override
    public Name getName() {
        return this.name;
    }
}

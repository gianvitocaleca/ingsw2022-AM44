package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.model.enums.Name;
import it.polimi.ingsw.model.Playable;

public class Thief implements Character {
    private final Name name;
    private final Playable model;
    private int updatedCost = 0;

    public Thief(Name name, Playable model) {
        this.name = name;
        this.model = model;
    }

    @Override
    public void effect(CharactersParameters answer) {
        //Thief will ask the controller for a creature to remove from the Dining Rooms
        model.thiefEffect(answer.getProvidedSourceCreatures().get(0));
    }


    @Override
    public Name getName() {
        return this.name;
    }

    @Override
    public int getCost() {
        return name.getCost() + updatedCost;
    }

    @Override
    public boolean hasCoin() {
        return (updatedCost == 1);
    }

    @Override
    public void setUpdatedCost() {
        updatedCost = 1;
    }
}

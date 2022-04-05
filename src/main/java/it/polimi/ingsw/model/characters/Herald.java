package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.model.enums.Name;
import it.polimi.ingsw.model.Playable;

public class Herald implements Character {

    private Name name;
    private Playable model;
    private int updatedCost=0;

    public Herald(Name name, Playable model) {
        this.name=name;
        this.model=model;
    }

    public void effect(CharactersParameters answer) {
        model.setHeraldIsland(answer.getProvidedIslandIndex());
    }

    @Override
    public Name getName() {
        return this.name;
    }

    @Override
    public int getCost() {
        return name.getCost()+updatedCost;
    }

    @Override
    public boolean hasCoin() {
        return (updatedCost==1);
    }

    @Override
    public void setUpdatedCost() {
        updatedCost=1;
    }
}

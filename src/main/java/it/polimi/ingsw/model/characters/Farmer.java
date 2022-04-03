package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.enums.Name;

public class Farmer implements Character{

    private Name name;
    private Playable model;
    private int updatedCost=0;

    public Farmer(Name name, Playable model) {
        this.name = name;
        this.model = model;
    }


    @Override
    public void effect() {
        updatedCost = 1;
        model.setFarmer();
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
    public void setCharactersParameters(CharactersParameters parameters) {

    }
}

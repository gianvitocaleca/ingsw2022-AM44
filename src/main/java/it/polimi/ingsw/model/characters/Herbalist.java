package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.messages.CharactersParameters;
import it.polimi.ingsw.model.enums.Name;
import it.polimi.ingsw.model.Playable;

public class Herbalist implements Character {
    private Name name;
    private Playable model;
    private int updatedCost=0;

    public Herbalist(Name name, Playable model) {
        this.name = name;
        this.model = model;
        model.setDeactivators(4);
    }

    @Override
    public boolean canBePlayed(int playerCoins) {
        if(playerCoins>=getCost()&&model.getDeactivators()>0){
            return true;
        }
        else{
            return false;
        }
    }

    public boolean effect(CharactersParameters answer) {
            model.setDeactivators(model.getDeactivators()-1);
            model.addNoEntry(answer.getProvidedIslandIndex());

            return true;
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

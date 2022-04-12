package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.model.Playable;
import it.polimi.ingsw.model.enums.Name;

public class Minstrel implements Character {
    private Name name;
    private Playable model;
    private int updatedCost = 0;

    public Minstrel(Name name, Playable model) {
        this.name = name;
        this.model = model;
    }

    @Override
    public boolean canBePlayed(int playerCoins) {
        if (playerCoins >= getCost()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean effect(CharactersParameters answer) {
        if (model.minstrelEffect(answer.getProvidedSourceCreatures(), answer.getProvidedDestinationCreatures())) {
            return true;
        }
        return false;
    }

    @Override
    public Name getName() {
        return name;
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

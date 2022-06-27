package it.polimi.ingsw.network.server;

import it.polimi.ingsw.model.enums.Name;

public class CharacterInformation {
    private Name name;
    private int cost;
    private int index;

    public CharacterInformation(Name name, int cost, int index) {
        this.name = name;
        this.cost = cost;
        this.index = index;
    }

    public Name getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }

    public int getCost() {
        return cost;
    }
}

package it.polimi.ingsw.server;

import it.polimi.ingsw.server.model.enums.Creature;
import it.polimi.ingsw.server.model.enums.Name;

import java.util.ArrayList;
import java.util.List;

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

package it.polimi.ingsw.network.server;

import it.polimi.ingsw.model.enums.Name;

public class CharacterInformation {
    private Name name;
    private int cost;
    private int index;

    /**
     * Used to store the info of a character
     * @param name is the character's name
     * @param cost is the character's cost
     * @param index is the character's index
     */
    public CharacterInformation(Name name, int cost, int index) {
        this.name = name;
        this.cost = cost;
        this.index = index;
    }

    /**
     *
     * @return is the character's name
     */
    public Name getName() {
        return name;
    }

    /**
     *
     * @return is the character's index
     */
    public int getIndex() {
        return index;
    }

    /**
     *
     * @return is the character's cost
     */
    public int getCost() {
        return cost;
    }
}

package it.polimi.ingsw;

public abstract class Character {
    protected GameModel model;
    protected int updated_cost;
    protected Name name;

    public Character() {

    }

    public Character(Name name) {
        this.name = name;
    }

    public int getCost() {
        return 0;
    }

    public void effect() {

    }
}

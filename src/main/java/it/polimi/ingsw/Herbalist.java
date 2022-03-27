package it.polimi.ingsw;

public class Herbalist implements Character {
    private int deactivator;
    private Name name;
    private Playable model;

    public Herbalist(Name name) {
        this.name = name;
    }

    public void effect() {

    }

    @Override
    public Name getName() {
        return this.name;
    }
}

package it.polimi.ingsw;

public class Thief implements Character {
    private final Name name;
    private Playable model;

    public Thief(Name name) {
        this.name = name;
    }

    public void effect() {

    }

    @Override
    public Name getName() {
        return this.name;
    }
}

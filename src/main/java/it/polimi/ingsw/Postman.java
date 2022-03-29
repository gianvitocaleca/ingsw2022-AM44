package it.polimi.ingsw;

public class Postman implements Character {

    private Name name;
    private Playable model;

    public Postman(Name name, Playable model) {
        this.name = name;

    }

    public void effect() {

    }

    @Override
    public Name getName() {
        return this.name;
    }
}

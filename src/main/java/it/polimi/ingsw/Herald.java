package it.polimi.ingsw;

public class Herald implements Character {

    private Name name;
    private Playable model;

    public Herald(Name name, Playable model) {
        this.name=name;
        this.model=model;
    }

    public void effect() {

    }

    @Override
    public Name getName() {
        return this.name;
    }
}

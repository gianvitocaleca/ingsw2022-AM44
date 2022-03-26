package it.polimi.ingsw;

public class Thief implements Character {
    private final Name name;

    public Thief(Name name) {
        this.name=name;
    }

    public void effect() {

    }

    @Override
    public Name getName() {
        return this.name;
    }
}

package it.polimi.ingsw;

public class Herald implements Character {

    private Name name;

    public Herald(Name name) {
        this.name=name;
    }

    public void effect() {

    }

    @Override
    public Name getName() {
        return this.name;
    }
}

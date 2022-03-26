package it.polimi.ingsw;

public class Postman implements Character {

    private Name name;

    public Postman(Name name) {
        this.name = name;
    }

    public void effect() {

    }

    @Override
    public Name getName() {
        return this.name;
    }
}

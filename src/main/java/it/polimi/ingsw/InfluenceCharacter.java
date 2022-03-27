package it.polimi.ingsw;

public class InfluenceCharacter implements Character {

    private Name name;
    private Playable model;

    public InfluenceCharacter(Name name) {
        this.name = name;
    }

    @Override
    public void effect() {

    }

    @Override
    public Name getName() {
        return this.name;
    }
}

package it.polimi.ingsw;

public class InfluenceCharacter implements Character {

    private Name name;
    private Playable model;

    public InfluenceCharacter(Name name, Playable model) {
        this.name = name;
        this.model = model;
    }

    @Override
    public void effect() {

    }

    @Override
    public Name getName() {
        return this.name;
    }
}

package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.model.enums.Name;
import it.polimi.ingsw.model.Playable;

public class Herbalist implements Character {
    private int deactivator;
    private Name name;
    private Playable model;

    public Herbalist(Name name, Playable model) {
        this.name = name;
        this.model = model;
    }

    public void effect() {

    }

    @Override
    public Name getName() {
        return this.name;
    }
}

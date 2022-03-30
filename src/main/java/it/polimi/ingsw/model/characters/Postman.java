package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.model.enums.Name;
import it.polimi.ingsw.model.Playable;

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

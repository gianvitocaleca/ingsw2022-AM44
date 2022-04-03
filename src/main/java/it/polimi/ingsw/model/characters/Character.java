package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.model.enums.Name;

public interface Character {

    void effect(CharactersParameters answer);

    Name getName();

    int getCost();

    boolean hasCoin();

    void setUpdatedCost();
}

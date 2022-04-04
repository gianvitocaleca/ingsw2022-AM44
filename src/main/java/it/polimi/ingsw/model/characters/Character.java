package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.model.enums.Name;
import it.polimi.ingsw.model.exceptions.StudentsOutOfStockException;

public interface Character {

    void effect(CharactersParameters answer) throws StudentsOutOfStockException;

    Name getName();

    int getCost();

    boolean hasCoin();

    void setUpdatedCost();
}

package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.model.enums.Name;

public interface Character {

    boolean canBePlayed(int playerCoins);

    boolean effect(CharactersParameters answer);

    Name getName();

    int getCost();

    boolean hasCoin();

    void setUpdatedCost();
}

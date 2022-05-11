package it.polimi.ingsw.server.model.characters;

import it.polimi.ingsw.server.networkMessages.CharactersParametersPayload;
import it.polimi.ingsw.server.model.enums.Name;

public interface Character {

    boolean canBePlayed(int playerCoins);

    boolean effect(CharactersParametersPayload answer);

    Name getName();

    int getCost();

    boolean hasCoin();

    void setUpdatedCost();
}
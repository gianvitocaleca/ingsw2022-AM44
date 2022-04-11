package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.model.GameModel;
import it.polimi.ingsw.model.enums.Name;

public interface CharacterCreator {

    public Character createCharacter(Name name, GameModel model);
}

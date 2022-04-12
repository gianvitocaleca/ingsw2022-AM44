package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.model.GameModel;
import it.polimi.ingsw.model.Playable;
import it.polimi.ingsw.model.enums.Name;
import it.polimi.ingsw.model.studentcontainers.StudentContainer;

public interface CharacterCreator {

    public Character createCharacter(Name name, Playable model);
}

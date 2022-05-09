package it.polimi.ingsw.server.model.characters;

import it.polimi.ingsw.server.model.Playable;
import it.polimi.ingsw.server.model.enums.Name;

public interface CharacterCreator {

    public Character createCharacter(Name name, Playable model);
}

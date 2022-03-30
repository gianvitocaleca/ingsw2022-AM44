package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.model.Playable;
import it.polimi.ingsw.model.characters.Character;
import it.polimi.ingsw.model.enums.Name;

public interface CharacterCreator {

    public Character createCharacter(Name name, Playable model);
}

package it.polimi.ingsw.characterTests;

import it.polimi.ingsw.model.characters.Character;
import it.polimi.ingsw.model.enums.Name;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

class CharacterCreatorTest {
    public Name name;
    public Character character;

    /**
     * Creates a random character
     */

    @BeforeEach
    public void InitializeCharacter() {
        List<Name> names = new ArrayList<Name>(Arrays.asList(Name.values()));
        name = names.get(new Random().nextInt(names.size()));
    }
}
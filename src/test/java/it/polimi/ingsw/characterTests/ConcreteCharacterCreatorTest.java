package it.polimi.ingsw.characterTests;

import it.polimi.ingsw.model.characters.Character;
import it.polimi.ingsw.model.characters.ConcreteCharacterCreator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConcreteCharacterCreatorTest {
    /**
     * This tests that when no nome is provided to ConcreteCharacterCreator, it generates a null character
     */
    @Test
    public void nullCharacterCreatorTest() {
        ConcreteCharacterCreator ccc = new ConcreteCharacterCreator();
        Character c = ccc.createCharacter(null, null);
        assertNull(c);
    }
}
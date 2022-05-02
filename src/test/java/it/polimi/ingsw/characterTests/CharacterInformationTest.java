package it.polimi.ingsw.characterTests;

import it.polimi.ingsw.server.model.characters.CharacterInformation;
import it.polimi.ingsw.server.model.enums.Creature;
import it.polimi.ingsw.server.model.enums.Name;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CharacterInformationTest {

    /**
     * This tests that when a characterInformation object is created, it contains the correct informations
     */
    @Test
    public void correctValuesTest() {
        Name name = Name.JOKER;
        boolean updatedCost = true;
        int deactivators = 0;
        int index = 2;
        List<Creature> creatures = new ArrayList<>();

        CharacterInformation ci = new CharacterInformation(name, updatedCost, deactivators, index, creatures);

        assertEquals(name, ci.getName());
        assertEquals(updatedCost, ci.isUpdatedCost());
        assertEquals(deactivators, ci.getDeactivators());
        assertEquals(index, ci.getIndex());
        assertEquals(creatures, ci.getMoverContent());
    }
}

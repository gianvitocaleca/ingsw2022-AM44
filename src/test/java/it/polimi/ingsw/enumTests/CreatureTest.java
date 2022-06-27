package it.polimi.ingsw.enumTests;

import it.polimi.ingsw.model.enums.Creature;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CreatureTest {
    /**
     * This tests the correctness of the getName() method from the Creature Enum
     */
    @Test
    public void yellowGnomesCorrectValueTest() {
        String name = "Table/yellow.png";
        assertEquals(name, Creature.YELLOW_GNOMES.getImage());
    }
}
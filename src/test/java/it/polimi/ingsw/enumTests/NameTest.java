package it.polimi.ingsw.enumTests;

import it.polimi.ingsw.model.enums.Name;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NameTest {
    /**
     * This tests that the Herald Name contains the correct values to ask
     */
    @Test
    public void heraldValuesTest() {
        int cost = 3;
        int maxMoves = 1;
        boolean needsSourceCreature = false;
        boolean needsIslandIndex = true;
        boolean needsMnMovements = false;
        boolean needsDestination = false;
        boolean needsDestinationCreature = false;

        assertEquals(cost, Name.HERALD.getCost());
        assertEquals(maxMoves, Name.HERALD.getMaxMoves());
        assertEquals(needsSourceCreature, Name.HERALD.isNeedsSourceCreature());
        assertEquals(needsIslandIndex, Name.HERALD.isNeedsIslandIndex());
        assertEquals(needsMnMovements, Name.HERALD.isNeedsMnMovements());
        assertEquals(needsDestination, Name.HERALD.isNeedsDestination());
        assertEquals(needsDestinationCreature, Name.HERALD.isNeedsDestinationCreature());

    }
}
package it.polimi.ingsw.playerTests;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.model.enums.Color;
import it.polimi.ingsw.model.enums.Creature;
import it.polimi.ingsw.model.enums.Value;
import it.polimi.ingsw.model.enums.Wizard;
import it.polimi.ingsw.model.player.Assistant;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.studentcontainers.Entrance;
import it.polimi.ingsw.model.students.Student;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class PlayerTest {

    /**
     * This tests that the created player has the provided username
     */
    @Test
    void getUsernameCorrectly() {
        Player p1 = new Player("userName", Color.BLACK, 1, Wizard.BLUE, 8, new Entrance(7));
        assertEquals(p1.getUsername(), "userName");
    }

    /**
     * This tests that the created player has the provided color
     */
    @Test
    void getMyCorrectColor() {
        Player p1 = new Player("userName", Color.BLACK, 1, Wizard.BLUE, 8, new Entrance(7));
        assertEquals(p1.getMyColor(), Color.BLACK);
    }

    /**
     * This tests that when an assistant card is played, the lastPlayedCard parameter is correctly set
     */
    @Test
    void getLastPlayedCard() {
        Player p1 = new Player("userName", Color.BLACK, 1, Wizard.BLUE, 8, new Entrance(7));
        Assistant a = new Assistant(Value.CAT);
        p1.setAssistantCard(a);
        assertEquals(p1.getLastPlayedCard(), a);
        Assistant b = new Assistant(Value.DOG);
        assertFalse(p1.getLastPlayedCard().equals(b));
        p1.setAssistantCard(b);
        assertEquals(p1.getLastPlayedCard(), b);
    }

    /**
     * Verifies the rules to add coins to the player
     * Does not give the coin the second time for the same position
     */
    @Test
    void checkCoinGiverTest() {
        Player p = new Player("userName", Color.BLACK, 1, Wizard.BLUE, 8, new Entrance(7));
        for (int i = 0; i < 3; i++) {
            p.getDiningRoom().addStudent(new Student(Creature.BLUE_UNICORNS));
        }
        assertEquals(true, p.checkCoinGiver(Creature.BLUE_UNICORNS));

        for (int i = 0; i < 3; i++) {
            p.getDiningRoom().addStudent(new Student(Creature.BLUE_UNICORNS));
        }
        p.getDiningRoom().addStudent(new Student(Creature.YELLOW_GNOMES));
        assertEquals(true, p.checkCoinGiver(Creature.BLUE_UNICORNS));
        assertEquals(false, p.checkCoinGiver(Creature.YELLOW_GNOMES));

        p.getDiningRoom().removeStudent(Creature.BLUE_UNICORNS);
        assertEquals(false, p.checkCoinGiver(Creature.BLUE_UNICORNS));
    }


}
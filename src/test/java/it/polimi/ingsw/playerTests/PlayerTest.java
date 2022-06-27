package it.polimi.ingsw.playerTests;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.model.exceptions.GameEndedException;
import it.polimi.ingsw.model.GameModel;
import it.polimi.ingsw.model.enums.Color;
import it.polimi.ingsw.model.enums.Creature;
import it.polimi.ingsw.model.enums.Assistants;
import it.polimi.ingsw.model.enums.Wizard;
import it.polimi.ingsw.model.exceptions.AssistantAlreadyPlayedException;
import it.polimi.ingsw.model.exceptions.PlanningPhaseEndedException;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.studentcontainers.DiningRoom;
import it.polimi.ingsw.model.studentcontainers.Entrance;
import it.polimi.ingsw.model.students.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

class PlayerTest {

    GameModel gm;


    /**
     * This creates a new GameModel instance to use in every test
     */
    @BeforeEach
    public void createGameModel() {
        gm = new GameModel(true,
                new ArrayList<>(Arrays.asList("Paolo", "Gianvito", "Sabrina")),
                3,
                new ArrayList<>(Arrays.asList(Color.values())),
                new ArrayList<>(Arrays.asList(Wizard.GANDALF, Wizard.SABRINA, Wizard.BALJEET)));
    }

    /**
     * This tests that the created player has the provided username
     */
    @Test
    void getUsernameCorrectly() {
        Player p1 = new Player("userName", Color.BLACK, 1, Wizard.BALJEET, 8, new Entrance(7));
        assertEquals(p1.getUsername(), "userName");
    }

    /**
     * This tests that the created player has the provided color
     */
    @Test
    void getMyCorrectColor() {
        Player p1 = new Player("userName", Color.BLACK, 1, Wizard.BALJEET, 8, new Entrance(7));
        assertEquals(p1.getMyColor(), Color.BLACK);
    }

    /**
     * This tests that when an assistant card is played, the lastPlayedCard parameter is correctly set
     */
    @Test
    void getLastPlayedCard() {

        try {
            gm.playAssistant(0);
        } catch (AssistantAlreadyPlayedException | PlanningPhaseEndedException | GameEndedException ignore) {
        }
        Player p1 = gm.getPlayers().get(0);
        assertEquals(p1.getLastPlayedCard().getName(), Assistants.CHEETAH);

    }

    /**
     * Verifies the rules to add coins to the player
     * Does not give the coin the second time for the same position
     */
    @Test
    void checkCoinGiverTest() {
        Player p = new Player("userName", Color.BLACK, 1, Wizard.BALJEET, 8, new Entrance(7));
        DiningRoom pDining = p.getDiningRoom();
        for (int i = 0; i < 3; i++) {
            pDining.addStudent(new Student(Creature.BLUE_UNICORNS));
        }
        p.setDiningRoom(pDining);
        assertEquals(true, p.checkCoinGiver(Creature.BLUE_UNICORNS));

        pDining = p.getDiningRoom();
        for (int i = 0; i < 3; i++) {
            pDining.addStudent(new Student(Creature.BLUE_UNICORNS));
        }
        pDining.addStudent(new Student(Creature.YELLOW_GNOMES));
        p.setDiningRoom(pDining);
        assertEquals(true, p.checkCoinGiver(Creature.BLUE_UNICORNS));
        assertEquals(false, p.checkCoinGiver(Creature.YELLOW_GNOMES));

        pDining = p.getDiningRoom();
        pDining.removeStudent(Creature.BLUE_UNICORNS);
        p.setDiningRoom(pDining);
        assertEquals(false, p.checkCoinGiver(Creature.BLUE_UNICORNS));
    }

    /**
     * This tests that the method setMyCoins works correctly
     */
    @Test
    public void setCoinsTest() {
        Player p1 = new Player("userName", Color.BLACK, 1, Wizard.BALJEET, 8, new Entrance(7));
        int numberOfCoins = 5;
        p1.setMyCoins(numberOfCoins);
        assertEquals(numberOfCoins, p1.getMyCoins());
    }

    /**
     * This tests that the method setTowers works correctly
     */
    @Test
    public void setTowersTest() {
        Player p1 = new Player("userName", Color.BLACK, 1, Wizard.BALJEET, 8, new Entrance(7));
        int numberOfTowers = 5;
        p1.setTowers(numberOfTowers);
        assertEquals(numberOfTowers, p1.getTowers());
    }

    /**
     * This tests that the method addTowers works correctly
     */
    @Test
    public void addTowersTest() {
        Player p1 = new Player("userName", Color.BLACK, 1, Wizard.BALJEET, 8, new Entrance(7));
        int numberOfTowers = 3;
        p1.setTowers(numberOfTowers);
        int towersToAdd = 2;
        p1.addTowers(towersToAdd);
        assertEquals(towersToAdd + numberOfTowers, p1.getTowers());
    }


}
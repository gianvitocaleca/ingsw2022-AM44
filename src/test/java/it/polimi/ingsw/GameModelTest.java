package it.polimi.ingsw;

import it.polimi.ingsw.model.GameModel;
import it.polimi.ingsw.model.enums.*;
import it.polimi.ingsw.model.exceptions.GroupsOfIslandsException;
import it.polimi.ingsw.model.exceptions.StudentsOutOfStockException;
import it.polimi.ingsw.model.player.*;
import it.polimi.ingsw.model.studentcontainers.Cloud;
import it.polimi.ingsw.model.students.Student;
import it.polimi.ingsw.model.students.StudentBucket;
import it.polimi.ingsw.model.characters.Character;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class GameModelTest {
    GameModel gm;
    /**
     *This reset the singleton StudentBucket after each test of this class
     * */
    @AfterEach
    public void resetBucket() {
        StudentBucket.resetMap();
    }

    /**
     * This create a new GameModel instance to use in every test
     */
    @BeforeEach
    public void createGameModel() {
        StudentBucket.resetMap();
        gm = new GameModel(false,
                new ArrayList<String>(Arrays.asList("Paolo", "Gianvito", "Sabrina")),
                3,
                new ArrayList<Color>(Arrays.asList(Color.values())),
                new ArrayList<Wizard>(Arrays.asList(Wizard.YELLOW, Wizard.PINK, Wizard.BLUE)));
    }

    /**
     * This test verifies that clouds are filled in the correct way
     */
    @Test
    void fillCloudsCorrectly() {
        gm.fillClouds();
        for (Cloud c : gm.getTable().getClouds()) {
            assertEquals(c.getStudents().size(), c.getCapacity());
        }
    }

    /**
     * This test verfies the correct behavoiur of assistantDeck and lastPlayedCard when every assistant is played.
     */
    @Test
    void playEveryAssistant() {
        for (int i = 0; i < 10; i++) {
            gm.playAssistant(0);
            assertEquals(gm.getPlayers().get(gm.getCurrentPlayerIndex()).getAssistantDeck().size(), 9 - i);
            assertEquals(gm.getPlayers().get(gm.getCurrentPlayerIndex()).getLastPlayedCards().size(), 1 + i);
        }
    }

    /**
     * This test verifies that the arraylist of players is orderd in the correct way
     * after each player played the assistant card.
     */
    @Test
    void establishRoundOrderCorrectly() {
        for (int i = 0; i < gm.getNumberOfPlayers(); i++) {
            gm.setCurrentPlayerIndex(i);
            gm.playAssistant(i);
        }
        gm.establishRoundOrder();
        gm.getPlayers().stream().forEach(System.out::println);
        assertTrue(gm.getPlayers().get(0).getLastPlayedCard().getValue() < gm.getPlayers().get(1).getLastPlayedCard().getValue());
        assertTrue(gm.getPlayers().get(1).getLastPlayedCard().getValue() < gm.getPlayers().get(2).getLastPlayedCard().getValue());
        assertTrue(gm.getPlayers().get(0).getLastPlayedCard().getValue() < gm.getPlayers().get(2).getLastPlayedCard().getValue());
    }

    /**
     * This test verifies one of the condition of game ended:
     * on the table there are only three islands left.
     */
    @Test
    public void LastFusionEndgameTest() {
        while (true) {
            try {
                gm.getTable().islandFusion("Both");
            } catch (GroupsOfIslandsException e) {
                break;
            }
        }
        assertTrue(gm.checkEndGame());
    }

    /**
     * This test verifies the correct behavoiur of the method moveStudents.
     */
    @Test
    void moveStudentsTest() {
        gm.fillClouds();
        Cloud zero = gm.getTable().getClouds().get(0);
        Cloud one = gm.getTable().getClouds().get(1);

        int finalSize = zero.getStudents().size() + one.getStudents().size();

        List<Creature> creaturesList = new ArrayList<>();
        List<Student> studentsList = new ArrayList<>();

        for (Student s : zero.getStudents()) {
            creaturesList.add(s.getCreature());
            studentsList.add(s);
        }
        for (Student s : one.getStudents()) {
            studentsList.add(s);
        }

        gm.moveStudents(zero, one, creaturesList);


        assertEquals(zero.getStudents().size(), 0);
        assertEquals(one.getStudents().size(), finalSize);
        assertTrue(one.getStudents().containsAll(studentsList));
    }

    /**
     * This test verifies that mother nature has the right position after a certain number of steps.
     */
    @Test
    void moveMotherNature() {
        int jumps = 10;
        int originalMnPos = gm.getTable().getMnPosition();
        if (jumps < ((gm.getTable().getIslands().size() - 1) - gm.getTable().getMnPosition())) {
            gm.moveMotherNature(jumps);
            assertTrue(gm.getTable().getMnPosition() == originalMnPos + jumps);
        } else {
            gm.moveMotherNature(jumps);
            assertTrue(gm.getTable().getMnPosition() == jumps - (gm.getTable().getIslands().size() - 2 - gm.getTable().getMnPosition()));
        }
    }

    /**
     * This test verifies the condition of gae ended:
     * a player has no tower left.
     */
    @Test
    void checkEndGameTowersFinished() {
        for (Player p : gm.getPlayers()) {
            assertEquals(p.getTowers(), 6);
            p.removeTowers(6);
            assertEquals(p.getTowers(), 0);
            assertTrue(gm.checkEndGame());
        }
    }

    /**
     * This test verifies the condition of game ended:
     * every player has played all the assistant cards.
     */
    @Test
    void checkEndGameEveryAssistantsPlayed() {
        for (int i = 0; i < 10; i++) {
            gm.playAssistant(0);
        }
        assertTrue(gm.checkEndGame());
    }

    /**
     * This test verifies the condition of game ended that all the students have been generated.
     */
    @Test
    void checkEndGameStudentsOutOfStock() {
        StudentBucket bucket = StudentBucket.getInstance();
        List<Student> temp = new ArrayList<>();
        while (true) {
            try {
                temp.add(bucket.generateStudent());
            } catch (StudentsOutOfStockException ex) {
                break;
            }
        }
        assertTrue(gm.checkEndGame());
    }

    /**
     * This test verifies the correct behaviour of the method findWinner.
     */
    @RepeatedTest(10)
    void findWinner() {
        List<Professor> professors = new ArrayList<Professor>();

        for (Creature c : Creature.values()) {
            professors.add(new Professor(c));
        }
        for (Player p : gm.getPlayers()) {
            p.removeTowers(new Random().nextInt(p.getTowers()));
            for (int i = 0; i <= new Random().nextInt(professors.size()); i++) {
                p.addProfessor(professors.get(new Random().nextInt(professors.size())));
            }

        }

        Player ans = gm.findWinner();
        for (Player p : gm.getPlayers()) {
            if (!ans.equals(p)) {
                assertTrue(ans.getTowers() < p.getTowers() ||
                        (ans.getTowers() == p.getTowers() &&
                                ans.getProfessors().size() >
                                        p.getProfessors().size()));
            }
        }
    }

    /**
     * This test verifies the correct behaviour of the method checkNeighbourIsland.
     */
    @Test
    void checkNeighbourIslandTest() {
        int oldSize = gm.getTable().getIslands().size();
        gm.getTable().getCurrentIsland().setColorOfTowers(Color.GREY);
        gm.getTable().getNextIsland().setColorOfTowers(Color.GREY);
        gm.checkNeighborIsland();
        assertEquals(oldSize - 1, gm.getTable().getIslands().size());
    }

    /**
     * This test verifies that the coins in the game are always 20, after a character has been played.
     */
    @Test
    void playCharacterTest() {

        gm = new GameModel(true,
                new ArrayList<String>(Arrays.asList("Paolo", "Gianvito", "Sabrina")),
                3,
                new ArrayList<Color>(Arrays.asList(Color.values())),
                new ArrayList<Wizard>(Arrays.asList(Wizard.YELLOW, Wizard.PINK, Wizard.BLUE)));
        //now table has 17 coins
        gm.setCurrentPlayerIndex(1);
        Character firstCharacter = gm.getCharacters().get(0);
        Player currentPlayer = gm.getPlayers().get(gm.getCurrentPlayerIndex());

        for (int i = 1; i < firstCharacter.getCost(); i++) {
            currentPlayer.addCoin();
            gm.getTable().removeCoin();
        }
        //now currentPlayer has exactly firstCharacter cost coins, table has 18 - (firstCharacter cost) coins

        //currentPlayer plays character(0), now he should have 0 coins, character(0) should have 1 coin in updatedCost,
        //table should have 17 coins again
        gm.playCharacter(0);
        assertTrue(currentPlayer.getMyCoins() == 0);
        assertTrue(firstCharacter.hasCoin());
        assertTrue(gm.getTable().getCoinReserve() == 18);


    }
}
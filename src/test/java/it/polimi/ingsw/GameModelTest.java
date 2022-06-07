package it.polimi.ingsw;

import it.polimi.ingsw.server.model.exceptions.GameEndedException;
import it.polimi.ingsw.server.model.GameModel;
import it.polimi.ingsw.server.model.characters.Character;
import it.polimi.ingsw.server.model.enums.Color;
import it.polimi.ingsw.server.model.enums.Creature;
import it.polimi.ingsw.server.model.enums.Assistants;
import it.polimi.ingsw.server.model.enums.Wizard;
import it.polimi.ingsw.server.model.exceptions.AssistantAlreadyPlayedException;
import it.polimi.ingsw.server.model.exceptions.GroupsOfIslandsException;
import it.polimi.ingsw.server.model.exceptions.PlanningPhaseEndedException;
import it.polimi.ingsw.server.model.gameboard.Table;
import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.server.model.player.Professor;
import it.polimi.ingsw.server.model.studentcontainers.Cloud;
import it.polimi.ingsw.server.model.studentcontainers.DiningRoom;
import it.polimi.ingsw.server.model.studentcontainers.Island;
import it.polimi.ingsw.server.model.students.Student;
import org.junit.jupiter.api.*;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class GameModelTest {
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
     * This test verifies that clouds are filled in the correct way
     */
    @Test
    public void fillCloudsCorrectly() {
        gm.fillClouds();
        for (Cloud c : gm.getTable().getClouds()) {
            assertEquals(c.getStudents().size(), c.getCapacity());
        }
    }

    /**
     * This test verifies that the arraylist of players is ordered in the correct way
     * after each player played the assistant card.
     */
    @Test
    void establishRoundOrderCorrectly() {
        for (int i = 0; i < gm.getNumberOfPlayers(); i++) {
            gm.setCurrentPlayerIndex(i);
            try {
                gm.playAssistant(i);
            } catch (AssistantAlreadyPlayedException | PlanningPhaseEndedException | GameEndedException ignore) {
            }
        }
        gm.establishRoundOrder();
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
            Table oldTable = gm.getTable();
            try {
                oldTable.islandFusion("Both");
                gm.setTable(oldTable);
            } catch (GroupsOfIslandsException e) {
                gm.setTable(oldTable);
                break;
            }
        }
        assertTrue(gm.checkEndGame());
    }

    /**
     * This test verifies the correct behavior of the method moveStudents.
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
        studentsList.addAll(one.getStudents());

        gm.moveStudents(zero, one, creaturesList);


        assertEquals(zero.getStudents().size(), 0);
        assertEquals(one.getStudents().size(), finalSize);
        for (Student s : one.getStudents()) {
            for (Student t : studentsList) {
                if (t.getCreature().equals(s.getCreature())) {
                    studentsList.remove(t);
                    break;
                }
            }
        }
        assertEquals(studentsList.size(), 0);
    }

    /**
     * Verifies che correct number of coins in the game,
     * checks that the coin's rules are met.
     */
    @Test
    public void moveStudentsInDiningRoomTest() {
        int totalGameCoins = 20;
        List<Creature> creat = gm.getPlayers().get(gm.getCurrentPlayerIndex()).getEntrance().getStudents().stream().map(Student::getCreature).collect(Collectors.toList());
        gm.moveStudents(gm.getPlayers().get(gm.getCurrentPlayerIndex()).getEntrance(),
                gm.getPlayers().get(gm.getCurrentPlayerIndex()).getDiningRoom(), creat);
        for (Creature c : Creature.values()) {
            assertFalse(gm.getPlayers().get(gm.getCurrentPlayerIndex()).checkCoinGiver(c));
        }
        int totalCoins = 0;
        for (Player p : gm.getPlayers()) {
            totalCoins += p.getMyCoins();
        }
        totalCoins += gm.getTable().getCoinReserve();
        assertEquals(totalGameCoins, totalCoins);

    }

    /**
     * This test verifies that mother nature has the right position after a certain number of steps.
     */
    @Test
    void moveMotherNature() {
        int jumps = 1;
        int index = gm.getCurrentPlayerIndex();
        int originalMnPos = gm.getTable().getMnPosition();
        if (jumps < ((gm.getTable().getIslands().size() - 1) - gm.getTable().getMnPosition())) {
            try {
                gm.playAssistant(9);
            } catch (AssistantAlreadyPlayedException | PlanningPhaseEndedException | GameEndedException ignore) {
            }
            gm.setCurrentPlayerIndex(index);
            try {
                gm.moveMotherNature(jumps);
            } catch (GameEndedException ignore) {

            }

            assertEquals(gm.getTable().getMnPosition(), originalMnPos + jumps);
        } else {
            try {
                gm.playAssistant(9);
            } catch (AssistantAlreadyPlayedException | PlanningPhaseEndedException | GameEndedException ignore) {
            }
            gm.setCurrentPlayerIndex(index);
            try {
                gm.moveMotherNature(jumps);
            } catch (GameEndedException ignore) {

            }
            assertEquals(gm.getTable().getMnPosition(), (originalMnPos + jumps) % gm.getTable().getIslands().size());
        }
    }

    /**
     * verifies that mother nature doesn't move if the number of steps overcome the value of the
     * assistant card played.
     */
    @Test
    void moveMotherNatureTooMuch() {
        int jumps = 12;
        int originalMNPos = gm.getTable().getMnPosition();
        try {
            gm.playAssistant(0);
            gm.setCurrentPlayerIndex(0);
        } catch (AssistantAlreadyPlayedException | PlanningPhaseEndedException | GameEndedException ignore) {
        }
        try {
            assertFalse(gm.moveMotherNature(jumps));
        } catch (GameEndedException ignore) {

        }
        assertEquals(originalMNPos, gm.getTable().getMnPosition());
    }

    /**
     * This test verifies the condition of gae ended:
     * a player has no tower left.
     */
    @Test
    void checkEndGameTowersFinished() throws GameEndedException {
        boolean gameEnded = false;
        List<Player> playerToSet = new ArrayList<>();
        for (Player p : gm.getPlayers()) {
            assertEquals(p.getTowers(), 6);
            try {
                p.removeTowers(6);
            } catch (GameEndedException e) {
                gameEnded = true;
            }
            assertEquals(p.getTowers(), 0);
            assertTrue(gameEnded);
            playerToSet.add(p);
        }
        gm.setPlayers(playerToSet);
        assertTrue(gm.checkEndGame());
    }

    /**
     * This test verifies the condition of game ended:
     * every player has played all the assistant cards.
     */
    @Test
    void checkEndGameEveryAssistantsPlayed() {
        for (int i = 0; i < Assistants.values().length; i++) {
            try {
                gm.playAssistant(0);
                gm.setCurrentPlayerIndex(0);
            } catch (AssistantAlreadyPlayedException | PlanningPhaseEndedException | GameEndedException ignore) {
            }
        }
        assertTrue(gm.checkEndGame());
    }


    /**
     * Simulates winning conditions by removing towers from the dashboard of the players
     * Also gives professors to the players
     */
    @Test
    void findWinner() throws GameEndedException {
        List<Professor> professors = new ArrayList<>();
        List<Player> tempPlayer = gm.getPlayers();
        for (Creature c : Creature.values()) {
            professors.add(new Professor(c));
        }
        for (Player p : tempPlayer) {
            p.removeTowers(new Random().nextInt(p.getTowers()));
        }
        //fixed distribution of professor in order to avoid winning condition issue
        tempPlayer.get(0).addProfessor(professors.get(0));
        tempPlayer.get(0).addProfessor(professors.get(1));
        tempPlayer.get(1).addProfessor(professors.get(2));
        tempPlayer.get(1).addProfessor(professors.get(3));
        tempPlayer.get(1).addProfessor(professors.get(4));
        gm.setPlayers(tempPlayer);

        Player ans = gm.findWinner();
        for (Player p : gm.getPlayers()) {
            if (!ans.getUsername().equals(p.getUsername())) {
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
        Table tempTable = gm.getTable();
        tempTable.setMotherNaturePosition(11);
        Island newIsland = tempTable.getCurrentIsland();
        newIsland.setColorOfTowers(Color.GREY);
        tempTable.setCurrentIsland(newIsland);
        newIsland = tempTable.getNextIsland();
        newIsland.setColorOfTowers(Color.GREY);
        tempTable.setNextIsland(newIsland);
        // tempTable.setIslands(tempTable.getIslands());
        gm.setTable(tempTable);
        try {
            gm.playAssistant(9);
        } catch (AssistantAlreadyPlayedException | PlanningPhaseEndedException | GameEndedException ignore) {
        }
        gm.setCurrentPlayerIndex(0);
        try {
            gm.moveMotherNature(1);
        } catch (GameEndedException ignore) {

        }
        assertEquals(oldSize - 1, gm.getTable().getIslands().size());
    }

    /**
     * This test verifies that the coins in the game are always 20, after a character has been played.
     */
    @Test
    void playCharacterTest() {

        gm = new GameModel(true,
                new ArrayList<>(Arrays.asList("Paolo", "Gianvito", "Sabrina")),
                3,
                new ArrayList<>(Arrays.asList(Color.values())),
                new ArrayList<>(Arrays.asList(Wizard.GANDALF, Wizard.SABRINA, Wizard.BALJEET)));
        //now table has 17 coins
        gm.setCurrentPlayerIndex(1);

        List<Player> players = gm.getPlayers();
        Character firstCharacter = gm.getCharacters().get(0);

        for (int i = 1; i < firstCharacter.getCost(); i++) {
            players.get(gm.getCurrentPlayerIndex()).addCoin();
            Table temp = gm.getTable();
            temp.removeCoin();
            gm.setTable(temp);
        }
        gm.setPlayers(players);
        //now currentPlayer has exactly firstCharacter cost coins, table has 18 - (firstCharacter cost) coins

        //currentPlayer plays character(0), now he should have 0 coins, character(0) should have 1 coin in updatedCost,
        //table should have 18 coins
        assertTrue(gm.playCharacter(0));
        Player currentPlayer = gm.getPlayers().get(gm.getCurrentPlayerIndex());
        assertEquals(0, currentPlayer.getMyCoins());
        firstCharacter = gm.getCharacters().get(0);
        assertTrue(firstCharacter.hasCoin());
        assertEquals(18, gm.getTable().getCoinReserve());
    }

    /**
     * This tests that when one player has more students of a creature than all the others in the game, it
     * gets the Professor corresponding to the creature
     */
    @Test
    public void checkProfessorTest() {

        List<Player> players = gm.getPlayers();

        DiningRoom room = new DiningRoom(9);
        room.addStudent(new Student(Creature.RED_DRAGONS));
        players.get(0).setDiningRoom(room);
        gm.setPlayers(players);
        gm.checkProfessor();
        assertTrue(gm.getPlayers().get(0).getProfessors().size() == 1);
        assertTrue(gm.getPlayers().get(0).getProfessors().get(0).getCreature().equals(Creature.RED_DRAGONS));

        players = gm.getPlayers();
        room = new DiningRoom(9);
        room.addStudent(new Student(Creature.RED_DRAGONS));
        room.addStudent(new Student(Creature.RED_DRAGONS));
        players.get(1).setDiningRoom(room);

        gm.setPlayers(players);
        gm.checkProfessor();
        assertTrue(gm.getPlayers().get(0).getProfessors().size() == 0);
        assertTrue(gm.getPlayers().get(1).getProfessors().size() == 1 && gm.getPlayers().get(1).getProfessors().get(0).getCreature().equals(Creature.RED_DRAGONS));
    }
    /**
    This test verifies that a player with the same number of students of one other player can't conquer
    the professor.
     */
    @Test
    public void checkProfessorWithSameStudentsInDiningRoom(){
        List<Player> players = gm.getPlayers();

        DiningRoom room = new DiningRoom(9);
        room.addStudent(new Student(Creature.RED_DRAGONS));
        players.get(0).setDiningRoom(room);
        gm.setCurrentPlayerIndex(0);
        gm.setPlayers(players);
        gm.checkProfessor();
        assertEquals(1, gm.getPlayers().get(0).getProfessors().size());
        assertEquals(gm.getPlayers().get(0).getProfessors().get(0).getCreature(), Creature.RED_DRAGONS);
        players.get(1).setDiningRoom(room);
        gm.setCurrentPlayerIndex(1);
        gm.checkProfessor();
        assertEquals(0, gm.getPlayers().get(1).getProfessors().size());
        assertEquals(1, gm.getPlayers().get(0).getProfessors().size());
        room.addStudent(new Student(Creature.YELLOW_GNOMES));
        room.addStudent(new Student(Creature.YELLOW_GNOMES));
        players.get(0).setDiningRoom(room);
        gm.setCurrentPlayerIndex(0);
        gm.setPlayers(players);
        gm.checkProfessor();
        assertEquals(2, gm.getPlayers().get(0).getProfessors().size());
        assertEquals(gm.getPlayers().get(0).getProfessors().get(1).getCreature(), Creature.YELLOW_GNOMES);
        DiningRoom diningRoom = new DiningRoom(9);
        diningRoom.addStudent(new Student(Creature.YELLOW_GNOMES));
        diningRoom.addStudent(new Student(Creature.YELLOW_GNOMES));
        players.get(1).setDiningRoom(diningRoom);
        gm.setCurrentPlayerIndex(1);
        gm.setPlayers(players);
        gm.checkProfessor();
        assertEquals(2, gm.getPlayers().get(0).getProfessors().size());
        assertEquals(gm.getPlayers().get(0).getProfessors().get(1).getCreature(), Creature.YELLOW_GNOMES);

    }
}
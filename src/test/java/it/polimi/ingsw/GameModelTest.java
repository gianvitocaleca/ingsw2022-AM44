package it.polimi.ingsw;

import it.polimi.ingsw.model.GameModel;
import it.polimi.ingsw.model.characters.*;
import it.polimi.ingsw.model.characters.Character;
import it.polimi.ingsw.model.enums.*;
import it.polimi.ingsw.model.exceptions.AssistantAlreadyPlayedException;
import it.polimi.ingsw.model.exceptions.GroupsOfIslandsException;
import it.polimi.ingsw.model.exceptions.PlanningPhaseEndedException;
import it.polimi.ingsw.model.exceptions.StudentsOutOfStockException;
import it.polimi.ingsw.model.gameboard.Table;
import it.polimi.ingsw.model.player.*;
import it.polimi.ingsw.model.studentcontainers.Cloud;
import it.polimi.ingsw.model.studentcontainers.Island;
import it.polimi.ingsw.model.students.Student;
import it.polimi.ingsw.model.students.StudentBucket;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class GameModelTest {
    GameModel gm;


    /**
     * This create a new GameModel instance to use in every test
     */
    @BeforeEach
    public void createGameModel() {
        gm = new GameModel(true,
                new ArrayList<>(Arrays.asList("Paolo", "Gianvito", "Sabrina")),
                3,
                new ArrayList<>(Arrays.asList(Color.values())),
                new ArrayList<>(Arrays.asList(Wizard.YELLOW, Wizard.PINK, Wizard.BLUE)));
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
     * This test verifies that the arraylist of players is orderd in the correct way
     * after each player played the assistant card.
     */
    @Test
    void establishRoundOrderCorrectly() {
        for (int i = 0; i < gm.getNumberOfPlayers(); i++) {
            gm.setCurrentPlayerIndex(i);
            try {
                gm.playAssistant(i);
            }catch(AssistantAlreadyPlayedException e){
                e.printStackTrace();
            }catch(PlanningPhaseEndedException e){
                e.printStackTrace();
            }
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
        for (Student s : one.getStudents()) {
            //controlla che tutti i fra fraeggino
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
        List<Creature> creat = gm.getPlayers().get(gm.getCurrentPlayerIndex()).getEntrance().getStudents().stream().map(s -> s.getCreature()).collect(Collectors.toList());
        gm.moveStudents(gm.getPlayers().get(gm.getCurrentPlayerIndex()).getEntrance(),
                gm.getPlayers().get(gm.getCurrentPlayerIndex()).getDiningRoom(), creat);
        for (Creature c : Creature.values()) {
            assertEquals(false, gm.getPlayers().get(gm.getCurrentPlayerIndex()).checkCoinGiver(c));
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
        List<Player> playerToSet = new ArrayList<>();
        for (Player p : gm.getPlayers()) {
            assertEquals(p.getTowers(), 6);
            p.removeTowers(6);
            assertEquals(p.getTowers(), 0);
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
        for (int i = 0; i < Value.values().length; i++) {
            try {
                gm.playAssistant(0);
            }catch(AssistantAlreadyPlayedException e){
                e.printStackTrace();
            }catch(PlanningPhaseEndedException e){
                e.printStackTrace();
            }
        }
        assertTrue(gm.checkEndGame());
    }

    /**
     * This test verifies the condition of game ended that all the students have been generated.
     */
    @Test
    void checkEndGameStudentsOutOfStock() {
        StudentBucket bucket = gm.getBucket();
        List<Student> temp = new ArrayList<>();
        while (true) {
            try {
                temp.add(bucket.generateStudent());
            } catch (StudentsOutOfStockException ex) {
                gm.setBucket(bucket);
                break;
            }
        }
        assertTrue(gm.checkEndGame());
    }

    /**
     * Simulates winning conditions by removing towers from the dashboard of the players
     * Also gives professors to the players
     */
    @Test
    void findWinner() {
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
        gm.moveMotherNature(0);
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
                new ArrayList<>(Arrays.asList(Wizard.YELLOW, Wizard.PINK, Wizard.BLUE)));
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
        gm.playCharacter(0);
        Player currentPlayer = gm.getPlayers().get(gm.getCurrentPlayerIndex());
        assertTrue(currentPlayer.getMyCoins() == 0);
        firstCharacter = gm.getCharacters().get(0);
        assertTrue(firstCharacter.hasCoin());
        assertTrue(gm.getTable().getCoinReserve() == 18);


    }


    @Test
    void standardEvaluatorTest() {
        int yellowCounter = 0, redCounter = 0, blueCounter = 0, greenCounter = 0, pinkCounter = 0;
        StudentBucket bucket = gm.getBucket();

        resetEvaluateInfluence();

        try {
            int i = 10;

            do {
                gm.getTable().getIslands().get(0).addStudent(bucket.generateStudent());
                i--;
            } while (i > 0);

        } catch (StudentsOutOfStockException ignored) {

        }

        gm.setBucket(bucket);

        List<Student> toCount;

        for (Creature x : Creature.values()) {
            toCount = gm.getTable().getIslands().get(0).getStudents().stream()
                    .filter(c -> c.getCreature().equals(x)).collect(Collectors.toList());
            if (x.equals(Creature.YELLOW_GNOMES)) {
                yellowCounter = toCount.size();
            }
            if (x.equals(Creature.RED_DRAGONS)) {
                redCounter = toCount.size();
            }
            if (x.equals(Creature.GREEN_FROGS)) {
                greenCounter = toCount.size();
            }
            if (x.equals(Creature.BLUE_UNICORNS)) {
                blueCounter = toCount.size();
            }
            if (x.equals(Creature.PINK_FAIRIES)) {
                pinkCounter = toCount.size();
            }
        }

        gm.getPlayers().get(0).addProfessor(new Professor(Creature.YELLOW_GNOMES));
        gm.getPlayers().get(0).addProfessor(new Professor(Creature.RED_DRAGONS));

        gm.getPlayers().get(1).addProfessor(new Professor(Creature.GREEN_FROGS));
        gm.getPlayers().get(1).addProfessor(new Professor(Creature.BLUE_UNICORNS));

        gm.getPlayers().get(2).addProfessor(new Professor(Creature.PINK_FAIRIES));

        gm.getTable().getMotherNature().setCurrentIsland(0);

        gm.evaluateInfluence();

        if (yellowCounter + redCounter > greenCounter + blueCounter && yellowCounter + redCounter > pinkCounter) {
            assertTrue(gm.getTable().getIslands().get(0).getColorOfTowers().equals(gm.getPlayers().get(0).getMyColor()));
        } else if (greenCounter + blueCounter > yellowCounter + redCounter && greenCounter + blueCounter > pinkCounter) {
            assertTrue(gm.getTable().getIslands().get(0).getColorOfTowers().equals(gm.getPlayers().get(1).getMyColor()));
        } else if (pinkCounter > yellowCounter + redCounter && greenCounter + blueCounter < pinkCounter) {
            assertTrue(gm.getTable().getIslands().get(0).getColorOfTowers().equals(gm.getPlayers().get(2).getMyColor()));
        }


    }

    @Test
    void centaurEvaluatorTest() {
        int yellowCounter = 0, redCounter = 0, blueCounter = 0, greenCounter = 0, pinkCounter = 0;
        StudentBucket bucket = gm.getBucket();

        resetEvaluateInfluence();

        Character centaur = new BehaviorCharacter(Name.CENTAUR, gm);
        centaur.effect(new CharactersParameters(
                new ArrayList<>(), 0, 0, new Cloud(10), new ArrayList<>()));

        try {
            int i = 10;
            do {
                gm.getTable().getIslands().get(0).addStudent(bucket.generateStudent());
                i--;
            } while (i > 0);

        } catch (StudentsOutOfStockException ignored) {

        }

        gm.getTable().getIslands().get(0).setColorOfTowers(Color.BLACK);

        gm.setBucket(bucket);
        List<Student> toCount;

        for (Creature x : Creature.values()) {
            toCount = gm.getTable().getIslands().get(0).getStudents().stream()
                    .filter(c -> c.getCreature().equals(x)).collect(Collectors.toList());
            if (x.equals(Creature.YELLOW_GNOMES)) {
                yellowCounter = toCount.size();
            }
            if (x.equals(Creature.RED_DRAGONS)) {
                redCounter = toCount.size();
            }
            if (x.equals(Creature.GREEN_FROGS)) {
                greenCounter = toCount.size();
            }
            if (x.equals(Creature.BLUE_UNICORNS)) {
                blueCounter = toCount.size();
            }
            if (x.equals(Creature.PINK_FAIRIES)) {
                pinkCounter = toCount.size();
            }
        }

        gm.getPlayers().get(0).addProfessor(new Professor(Creature.YELLOW_GNOMES));
        gm.getPlayers().get(0).addProfessor(new Professor(Creature.RED_DRAGONS));

        gm.getPlayers().get(1).addProfessor(new Professor(Creature.GREEN_FROGS));
        gm.getPlayers().get(1).addProfessor(new Professor(Creature.BLUE_UNICORNS));

        gm.getPlayers().get(2).addProfessor(new Professor(Creature.PINK_FAIRIES));

        gm.getTable().getMotherNature().setCurrentIsland(0);

        gm.evaluateInfluence();

        if (yellowCounter + redCounter > greenCounter + blueCounter && yellowCounter + redCounter > pinkCounter) {
            assertTrue(gm.getTable().getIslands().get(0).getColorOfTowers().equals(gm.getPlayers().get(0).getMyColor()));
        } else if (greenCounter + blueCounter > yellowCounter + redCounter && greenCounter + blueCounter > pinkCounter) {
            assertTrue(gm.getTable().getIslands().get(0).getColorOfTowers().equals(gm.getPlayers().get(1).getMyColor()));
        } else if (pinkCounter > yellowCounter + redCounter && greenCounter + blueCounter < pinkCounter) {
            assertTrue(gm.getTable().getIslands().get(0).getColorOfTowers().equals(gm.getPlayers().get(2).getMyColor()));
        }
    }

    @Test
    void knightEvaluatorTest() {
        int yellowCounter = 0, redCounter = 0, blueCounter = 0, greenCounter = 0, pinkCounter = 0;
        StudentBucket bucket = gm.getBucket();

        resetEvaluateInfluence();

        Character knight = new BehaviorCharacter(Name.KNIGHT, gm);
        knight.effect(new CharactersParameters(
                new ArrayList<>(), 0, 0, new Cloud(10), new ArrayList<>()));

        try {
            int i = 10;
            do {
                gm.getTable().getIslands().get(0).addStudent(bucket.generateStudent());
                i--;
            } while (i > 0);

        } catch (StudentsOutOfStockException ignored) {

        }

        gm.getTable().getIslands().get(0).setColorOfTowers(Color.BLACK);
        gm.setBucket(bucket);
        List<Student> toCount;

        for (Creature x : Creature.values()) {
            toCount = gm.getTable().getIslands().get(0).getStudents().stream()
                    .filter(c -> c.getCreature().equals(x)).collect(Collectors.toList());
            if (x.equals(Creature.YELLOW_GNOMES)) {
                yellowCounter = toCount.size();
            }
            if (x.equals(Creature.RED_DRAGONS)) {
                redCounter = toCount.size();
            }
            if (x.equals(Creature.GREEN_FROGS)) {
                greenCounter = toCount.size();
            }
            if (x.equals(Creature.BLUE_UNICORNS)) {
                blueCounter = toCount.size();
            }
            if (x.equals(Creature.PINK_FAIRIES)) {
                pinkCounter = toCount.size();
            }
        }

        gm.getPlayers().get(0).addProfessor(new Professor(Creature.YELLOW_GNOMES));
        gm.getPlayers().get(0).addProfessor(new Professor(Creature.RED_DRAGONS));

        gm.getPlayers().get(1).addProfessor(new Professor(Creature.GREEN_FROGS));
        gm.getPlayers().get(1).addProfessor(new Professor(Creature.BLUE_UNICORNS));

        gm.getPlayers().get(2).addProfessor(new Professor(Creature.PINK_FAIRIES));

        gm.getTable().getMotherNature().setCurrentIsland(0);

        gm.evaluateInfluence();

        if (yellowCounter + redCounter + 2 + 1 > greenCounter + blueCounter && yellowCounter + redCounter + 2 > pinkCounter) {
            assertEquals(gm.getTable().getIslands().get(0).getColorOfTowers(), gm.getPlayers().get(0).getMyColor());
        } else if (greenCounter + blueCounter > yellowCounter + redCounter + 2 + 1 && greenCounter + blueCounter > pinkCounter) {
            assertEquals(gm.getTable().getIslands().get(0).getColorOfTowers(), gm.getPlayers().get(1).getMyColor());
        } else if (pinkCounter > yellowCounter + redCounter + 2 + 1 && greenCounter + blueCounter < pinkCounter) {
            assertEquals(gm.getTable().getIslands().get(0).getColorOfTowers(), gm.getPlayers().get(2).getMyColor());
        }
    }

    @Test
    void fungaroEvaluatorTest() {
        int yellowCounter = 0, redCounter = 0, blueCounter = 0, greenCounter = 0, pinkCounter = 0;
        StudentBucket bucket = gm.getBucket();

        resetEvaluateInfluence();

        Character fungaro = new BehaviorCharacter(Name.FUNGARO, gm);
        List<Creature> gnomes = new ArrayList<>();
        gnomes.add(Creature.YELLOW_GNOMES);

        fungaro.effect(new CharactersParameters(
                gnomes, 0, 0, new Cloud(10), new ArrayList<>()));

        try {
            int i = 10;
            do {
                gm.getTable().getIslands().get(0).addStudent(bucket.generateStudent());
                i--;
            } while (i > 0);

        } catch (StudentsOutOfStockException ignored) {

        }

        // gm.getTable().getIslands().get(0).setColorOfTowers(Color.BLACK);
        gm.setBucket(bucket);
        List<Student> toCount;

        for (Creature x : Creature.values()) {
            toCount = gm.getTable().getIslands().get(0).getStudents().stream()
                    .filter(c -> c.getCreature().equals(x)).collect(Collectors.toList());
            if (x.equals(Creature.YELLOW_GNOMES)) {
                yellowCounter = toCount.size();
            }
            if (x.equals(Creature.RED_DRAGONS)) {
                redCounter = toCount.size();
            }
            if (x.equals(Creature.GREEN_FROGS)) {
                greenCounter = toCount.size();
            }
            if (x.equals(Creature.BLUE_UNICORNS)) {
                blueCounter = toCount.size();
            }
            if (x.equals(Creature.PINK_FAIRIES)) {
                pinkCounter = toCount.size();
            }
        }

        gm.getPlayers().get(0).addProfessor(new Professor(Creature.YELLOW_GNOMES));
        gm.getPlayers().get(0).addProfessor(new Professor(Creature.RED_DRAGONS));

        gm.getPlayers().get(1).addProfessor(new Professor(Creature.GREEN_FROGS));
        gm.getPlayers().get(1).addProfessor(new Professor(Creature.BLUE_UNICORNS));

        gm.getPlayers().get(2).addProfessor(new Professor(Creature.PINK_FAIRIES));

        gm.getTable().getMotherNature().setCurrentIsland(0);

        gm.evaluateInfluence();

        //dovrebbe vincere con giallo ma non vince
        if (yellowCounter + redCounter > greenCounter + blueCounter && yellowCounter + redCounter > pinkCounter) {
            if (redCounter < greenCounter + blueCounter && redCounter < pinkCounter) {
                assertFalse(gm.getTable().getIslands().get(0).getColorOfTowers().equals(gm.getPlayers().get(0).getMyColor()));
            }
        }

        if (redCounter > greenCounter + blueCounter && redCounter > pinkCounter) {
            assertTrue(gm.getTable().getIslands().get(0).getColorOfTowers().equals(gm.getPlayers().get(0).getMyColor()));
        } else if (greenCounter + blueCounter > redCounter && greenCounter + blueCounter > pinkCounter) {
            assertTrue(gm.getTable().getIslands().get(0).getColorOfTowers().equals(gm.getPlayers().get(1).getMyColor()));

        } else if (pinkCounter > redCounter && greenCounter + blueCounter < pinkCounter) {
            assertTrue(gm.getTable().getIslands().get(0).getColorOfTowers().equals(gm.getPlayers().get(2).getMyColor()));
        }
    }

    void resetEvaluateInfluence() {
        gm = new GameModel(true,
                new ArrayList<>(Arrays.asList("Paolo", "Gianvito", "Sabrina")),
                3,
                new ArrayList<>(Arrays.asList(Color.values())),
                new ArrayList<>(Arrays.asList(Wizard.YELLOW, Wizard.PINK, Wizard.BLUE)));
    }

}
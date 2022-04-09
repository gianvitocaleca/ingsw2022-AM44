package it.polimi.ingsw;

import it.polimi.ingsw.model.GameModel;
import it.polimi.ingsw.model.characters.*;
import it.polimi.ingsw.model.characters.Character;
import it.polimi.ingsw.model.enums.*;
import it.polimi.ingsw.model.exceptions.GroupsOfIslandsException;
import it.polimi.ingsw.model.exceptions.StudentsOutOfStockException;
import it.polimi.ingsw.model.player.*;
import it.polimi.ingsw.model.studentcontainers.Cloud;
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
     * This reset the singleton StudentBucket after each test of this class
     */
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
     * This test verfies the correct behavoiur of assistantDeck and lastPlayedCard when every assistant is played.
     */
    @Test
    void playEveryAssistant() {
        for (int i = 0; i < Value.values().length; i++) {
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
        for (int i = 0; i < Value.values().length; i++) {
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
     * Simulates winning conditions by removing towers from the dashboard of the players
     * Also gives professors to the players
     */
    @Test
    void findWinner() {
        List<Professor> professors = new ArrayList<>();

        for (Creature c : Creature.values()) {
            professors.add(new Professor(c));
        }
        for (Player p : gm.getPlayers()) {
            p.removeTowers(new Random().nextInt(p.getTowers()));
        }
        //fixed distribution of professor in order to avoid winning condition issue
        gm.getPlayers().get(0).addProfessor(professors.get(0));
        gm.getPlayers().get(0).addProfessor(professors.get(1));
        gm.getPlayers().get(1).addProfessor(professors.get(2));
        gm.getPlayers().get(1).addProfessor(professors.get(3));
        gm.getPlayers().get(1).addProfessor(professors.get(4));

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
        Character firstCharacter = gm.getCharacters().get(0);
        Player currentPlayer = gm.getPlayers().get(gm.getCurrentPlayerIndex());

        for (int i = 1; i < firstCharacter.getCost(); i++) {
            currentPlayer.addCoin();
            gm.getTable().removeCoin();
        }
        //now currentPlayer has exactly firstCharacter cost coins, table has 18 - (firstCharacter cost) coins

        //currentPlayer plays character(0), now he should have 0 coins, character(0) should have 1 coin in updatedCost,
        //table should have 18 coins
        gm.playCharacter(0);
        assertTrue(currentPlayer.getMyCoins() == 0);
        assertTrue(firstCharacter.hasCoin());
        assertTrue(gm.getTable().getCoinReserve() == 18);


    }

    /**
     * This test verifies that herbalist's effect has the correct behaviour
     */
    @Test
    void herbalistEffectTest() {
        int islandIndex = new Random().nextInt(gm.getTable().getIslands().size());
        System.out.println("L'indice dell'isola è: " + islandIndex);
        CharactersParameters herbalist = new CharactersParameters(new ArrayList<>(), islandIndex, 0, new Cloud(12), new ArrayList<>());
        //set Herbalist Character in characters to test her effect.
        gm.getCharacters().remove(0);
        gm.getCharacters().add(0, new Herbalist(Name.HERBALIST, gm));
        gm.getPlayers().get(gm.getCurrentPlayerIndex()).addCoin();
        gm.getPlayers().get(gm.getCurrentPlayerIndex()).addCoin();
        gm.getPlayers().get(gm.getCurrentPlayerIndex()).addCoin();
        gm.playCharacter(0);
        gm.effect(herbalist);
        assertEquals(gm.getTable().getIslands().get(islandIndex).getNumberOfNoEntries(), 1);
    }

    /**
     * Removes the students from the dining room of the players
     * Checks if the student bucket correctly updated
     */

    @Test
    void thiefEffectTest() {
        StudentBucket sb = StudentBucket.getInstance();
        int[][] oldStudentsByPlayer = {{0, 0, 0, 0, 0}, {0, 0, 0, 0, 0}, {0, 0, 0, 0, 0}};
        ArrayList<Creature> cret = new ArrayList<>(Arrays.asList(Creature.values()));
        for (int j = 0; j < gm.getPlayers().size(); j++) {
            for (int i = 0; i < 10; i++) {
                try {
                    gm.getPlayers().get(j).getDiningRoom().addStudent(sb.generateStudent());
                } catch (StudentsOutOfStockException ignore) {
                }
            }
            //saves number of students generated randomly by player and creature
            for (int k = 0; k < cret.size(); k++) {
                oldStudentsByPlayer[j][k] = gm.getPlayers().get(j).getDiningRoom().getNumberOfStudentsByCreature(cret.get(k));
            }

        }
        List<Creature> uni = new ArrayList<>();
        uni.add(Creature.BLUE_UNICORNS);
        CharactersParameters thief = new CharactersParameters(uni, 0, 0, null, new ArrayList<>());
        gm.getCharacters().remove(0);
        gm.getCharacters().add(0, new Thief(Name.THIEF, gm));

        gm.getPlayers().get(gm.getCurrentPlayerIndex()).addCoin();
        gm.getPlayers().get(gm.getCurrentPlayerIndex()).addCoin();
        gm.getPlayers().get(gm.getCurrentPlayerIndex()).addCoin();

        assertTrue(gm.playCharacter(0));
        assertTrue(gm.effect(thief));
        for (int i = 0; i < gm.getPlayers().size(); i++) {
            for (int j = 0; j < cret.size(); j++) {
                if (!cret.get(j).equals(Creature.BLUE_UNICORNS)) {
                    assertEquals(oldStudentsByPlayer[i][j],
                            gm.getPlayers().get(i).getDiningRoom().getNumberOfStudentsByCreature(cret.get(j)));
                } else if (oldStudentsByPlayer[i][j] < 3) {
                    assertEquals(0, gm.getPlayers().get(i).getDiningRoom().getNumberOfStudentsByCreature(cret.get(j)));
                } else {
                    assertEquals(oldStudentsByPlayer[i][j] - 3, gm.getPlayers().get(i).getDiningRoom().getNumberOfStudentsByCreature(cret.get(j)));
                }

            }
        }

    }


    /**
     * This test verifies that herald has the correct behaviour.
     */
    @Test
    void heraldEffectTest() {
        int islandIndex = new Random().nextInt(gm.getTable().getIslands().size());
        CharactersParameters herald = new CharactersParameters(new ArrayList<>(), islandIndex, 0, new Cloud(12), new ArrayList<>());
        List<Professor> profes = new ArrayList<>();
        for (Creature c : Creature.values()) {
            profes.add(new Professor(c));
        }
        for (int i = 0; i < gm.getPlayers().size(); i++) {
            gm.getPlayers().get(i).addProfessor(profes.get(i));
        }
        gm.getPlayers().get(0).addProfessor(profes.get(3));
        gm.getPlayers().get(0).addProfessor(profes.get(4));
        //set Herald Character in characters to test her effect.
        gm.getCharacters().remove(0);
        gm.getCharacters().add(0, new Herald(Name.HERALD, gm));
        gm.getPlayers().get(gm.getCurrentPlayerIndex()).addCoin();
        gm.getPlayers().get(gm.getCurrentPlayerIndex()).addCoin();
        gm.getPlayers().get(gm.getCurrentPlayerIndex()).addCoin();
        gm.playCharacter(0);
        gm.effect(herald);
        for (Creature c : Creature.values()) {
            if (gm.getTable().getIslands().get(islandIndex).getNumberOfStudentsByCreature(c) == 1) {
                for (Player p : gm.getPlayers()) {
                    for (Professor prof : p.getProfessors()) {
                        if (prof.getCreature().equals(c)) {
                            //player that has influence on the island
                            assertEquals(gm.getTable().getIslands().get(islandIndex).getColorOfTowers(), p.getMyColor());

                        }
                    }
                }
            }
        }
    }

    @Test
    void standardEvaluatorTest() {
        int yellowCounter = 0, redCounter = 0, blueCounter = 0, greenCounter = 0, pinkCounter = 0;

        resetEvaluateInfluence();

        try {
            int i = 10;

            do {
                gm.getTable().getIslands().get(0).addStudent(StudentBucket.generateStudent());
                i--;
            } while (i > 0);

        } catch (StudentsOutOfStockException ignored) {

        }

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

        resetEvaluateInfluence();

        Character centaur = new BehaviorCharacter(Name.CENTAUR, gm);
        centaur.effect(new CharactersParameters(
                new ArrayList<>(), 0, 0, new Cloud(10), new ArrayList<>()));

        try {
            int i = 10;
            do {
                gm.getTable().getIslands().get(0).addStudent(StudentBucket.generateStudent());
                i--;
            } while (i > 0);

        } catch (StudentsOutOfStockException ignored) {

        }

        gm.getTable().getIslands().get(0).setColorOfTowers(Color.BLACK);

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

        resetEvaluateInfluence();

        Character knight = new BehaviorCharacter(Name.KNIGHT, gm);
        knight.effect(new CharactersParameters(
                new ArrayList<>(), 0, 0, new Cloud(10), new ArrayList<>()));

        try {
            int i = 10;
            do {
                gm.getTable().getIslands().get(0).addStudent(StudentBucket.generateStudent());
                i--;
            } while (i > 0);

        } catch (StudentsOutOfStockException ignored) {

        }

        gm.getTable().getIslands().get(0).setColorOfTowers(Color.BLACK);

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

        if (yellowCounter + redCounter + 2 > greenCounter + blueCounter && yellowCounter + redCounter + 2 > pinkCounter) {
            assertTrue(gm.getTable().getIslands().get(0).getColorOfTowers().equals(gm.getPlayers().get(0).getMyColor()));
        } else if (greenCounter + blueCounter > yellowCounter + redCounter + 2 && greenCounter + blueCounter > pinkCounter) {
            assertTrue(gm.getTable().getIslands().get(0).getColorOfTowers().equals(gm.getPlayers().get(1).getMyColor()));
        } else if (pinkCounter > yellowCounter + redCounter + 2 && greenCounter + blueCounter < pinkCounter) {
            assertTrue(gm.getTable().getIslands().get(0).getColorOfTowers().equals(gm.getPlayers().get(2).getMyColor()));
        }
    }

    @Test
    void fungaroEvaluatorTest() {
        int yellowCounter = 0, redCounter = 0, blueCounter = 0, greenCounter = 0, pinkCounter = 0;

        resetEvaluateInfluence();

        Character fungaro = new BehaviorCharacter(Name.FUNGARO, gm);
        List<Creature> gnomes = new ArrayList<>();
        gnomes.add(Creature.YELLOW_GNOMES);

        fungaro.effect(new CharactersParameters(
                gnomes, 0, 0, new Cloud(10), new ArrayList<>()));

        try {
            int i = 10;
            do {
                gm.getTable().getIslands().get(0).addStudent(StudentBucket.generateStudent());
                i--;
            } while (i > 0);

        } catch (StudentsOutOfStockException ignored) {

        }

        // gm.getTable().getIslands().get(0).setColorOfTowers(Color.BLACK);

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

    /**
     * Swaps students from Joker character and player entrance
     */
    @Test
    void JokerTest() {
        //create the MoverCharacter
        MoverCharacter joker = new MoverCharacter(Name.JOKER, gm, 6);
        //put the character in first position
        gm.getCharacters().remove(0);
        gm.getCharacters().add(0, joker);
        //play the first character
        gm.getPlayers().get(gm.getCurrentPlayerIndex()).addCoin();
        gm.getPlayers().get(gm.getCurrentPlayerIndex()).addCoin();
        gm.getPlayers().get(gm.getCurrentPlayerIndex()).addCoin();
        gm.playCharacter(0);


        //necessary students and creatures from the character and the entrance
        List<Student> studentsInJoker = joker.getStudents();
        List<Creature> oldJokerCreatures = new ArrayList<>();
        for (Student s : studentsInJoker) {
            oldJokerCreatures.add(s.getCreature());
        }
        //populate the current player entrance with random students
        for (int i = 0; i < studentsInJoker.size(); i++) {
            try {
                gm.getPlayers().get(gm.getCurrentPlayerIndex()).getEntrance().addStudent(StudentBucket.generateStudent());
            } catch (StudentsOutOfStockException ignore) {
            }
        }
        List<Student> studentsInEntrance = gm.getPlayers().get(gm.getCurrentPlayerIndex()).getEntrance().getStudents();
        List<Creature> oldEntranceCreatures = new ArrayList<>();
        for (Student s : studentsInEntrance) {
            oldEntranceCreatures.add(s.getCreature());
        }

        //creates the parameters for the character effect
        CharactersParameters jokerParameters = new CharactersParameters(oldJokerCreatures,
                0, 0, null, oldEntranceCreatures);
        //play character effect
        gm.effect(jokerParameters);
        //the number of students should be the same as before
        assertEquals(joker.getStudents().size(), 6);
        assertEquals(gm.getPlayers().get(gm.getCurrentPlayerIndex()).getEntrance().getStudents().size(), 6);

        //get the new creatures in the character and the entrance
        List<Creature> newJokerCreatures = new ArrayList<>();
        for (Student s : joker.getStudents()) {
            newJokerCreatures.add(s.getCreature());
        }
        List<Creature> newEntranceCreatures = new ArrayList<>();
        for (Student s : gm.getPlayers().get(gm.getCurrentPlayerIndex()).getEntrance().getStudents()) {
            newEntranceCreatures.add(s.getCreature());
        }

        //the creatures should be swapped
        assertEquals(oldJokerCreatures, newEntranceCreatures);
        assertEquals(oldEntranceCreatures, newJokerCreatures);

    }
}
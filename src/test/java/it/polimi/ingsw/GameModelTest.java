package it.polimi.ingsw;

import it.polimi.ingsw.model.GameModel;
import it.polimi.ingsw.model.characters.CharactersParameters;
import it.polimi.ingsw.model.characters.Herald;
import it.polimi.ingsw.model.characters.Herbalist;
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
     * Simulates winning conditions by removing towers from the dashboard of the players
     * Also gives professors to the players
     */
    @Test
    void findWinner() {
        List<Professor> professors = new ArrayList<Professor>();

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
        CharactersParameters herbalist = new CharactersParameters(new ArrayList<Creature>(), islandIndex, 0, new Cloud(12));
        //set Herbalist Character in characters to test her effect.
        gm.getCharacters().remove(0);
        gm.getCharacters().add(0, new Herbalist(Name.HERBALIST, gm));
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
        gm.thiefEffect(Creature.BLUE_UNICORNS);
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
    void heraldEffectTest(){
        int islandIndex = new Random().nextInt(gm.getTable().getIslands().size());
        CharactersParameters herald = new CharactersParameters(new ArrayList<Creature>(),islandIndex,0,new Cloud(12));
        List<Professor> profes = new ArrayList<>();
        for(Creature c : Creature.values()){
            profes.add(new Professor(c));
        }
        for( int i=0; i<gm.getPlayers().size(); i++){
            gm.getPlayers().get(i).addProfessor(profes.get(i));
        }
        gm.getPlayers().get(0).addProfessor(profes.get(3));
        gm.getPlayers().get(0).addProfessor(profes.get(4));
        //set Herald Character in characters to test her effect.
        gm.getCharacters().remove(0);
        gm.getCharacters().add(0,new Herald(Name.HERALD,gm));
        gm.playCharacter(0);
        gm.effect(herald);
        for(Creature c : Creature.values()){
            if(gm.getTable().getIslands().get(islandIndex).getNumberOfStudentsByCreature(c)==1){
                for(Player p : gm.getPlayers()){
                    for(Professor prof : p.getProfessors()){
                        if(prof.getCreature().equals(c)){
                            //player that has influence on the island
                            assertEquals(gm.getTable().getIslands().get(islandIndex).getColorOfTowers(), p.getMyColor());

                        }
                    }
                }
            }
        }
    }
}
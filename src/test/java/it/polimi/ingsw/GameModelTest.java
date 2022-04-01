package it.polimi.ingsw;

import it.polimi.ingsw.model.GameModel;
import it.polimi.ingsw.model.enums.*;
import it.polimi.ingsw.model.exceptions.GroupsOfIslandsException;
import it.polimi.ingsw.model.exceptions.StudentsOutOfStockException;
import it.polimi.ingsw.model.player.*;
import it.polimi.ingsw.model.studentcontainers.Cloud;
import it.polimi.ingsw.model.students.Student;
import it.polimi.ingsw.model.students.StudentBucket;
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

    @BeforeEach
    public void createGameModel() {
        gm = new GameModel(false,
                new ArrayList<String>(Arrays.asList("Paolo", "Gianvito", "Sabrina")),
                3,
                new ArrayList<Color>(Arrays.asList(Color.values())),
                new ArrayList<Wizard>(Arrays.asList(Wizard.YELLOW, Wizard.PINK, Wizard.BLUE)));
    }

    @Test
    void fillCloudsCorrectly() {
        gm.fillClouds();
        for (Cloud c : gm.getTable().getClouds()) {
            assertEquals(c.getStudents().size(), c.getCapacity());
        }
    }

    @Test
    void playEveryAssistant() {
        for (int i = 0; i < 10; i++) {
            gm.playAssistant(0);
            assertEquals(gm.getPlayers().get(gm.getCurrPlayer()).getAssistantDeck().size(), 9 - i);
            assertEquals(gm.getPlayers().get(gm.getCurrPlayer()).getLastPlayedCards().size(), 1 + i);
        }
    }

    @Test
    void establishRoundOrderCorrectly() {
        for(int i=0; i<gm.getNumberOfPlayers(); i++){
            gm.setCurrPlayer(i);
            gm.playAssistant(i);
        }
        gm.establishRoundOrder();
        gm.getPlayers().stream().forEach(System.out::println);
        assertTrue(gm.getPlayers().get(0).getLastPlayedCard().getValue()<gm.getPlayers().get(1).getLastPlayedCard().getValue());
        assertTrue(gm.getPlayers().get(1).getLastPlayedCard().getValue()<gm.getPlayers().get(2).getLastPlayedCard().getValue());
        assertTrue(gm.getPlayers().get(0).getLastPlayedCard().getValue()<gm.getPlayers().get(2).getLastPlayedCard().getValue());
    }

    @Test
    void moveStudents() {
    }

    @Test
    void moveMotherNature() {
    }

    @Test
    void checkEndGameTowersFinished() {
        for(Player p: gm.getPlayers()){
            assertEquals(p.getTowers(),6);
            p.removeTowers(6);
            assertEquals(p.getTowers(),0);
            assertTrue(gm.checkEndGame());
        }
    }

    @Test
    void checkEndGameEveryAssistantsPlayed(){
        for (int i = 0; i < 10; i++) {
            gm.playAssistant(0);
        }
        assertTrue(gm.checkEndGame());
    }

    @Test
    void checkEndGameStudentsOutOfStock(){
        StudentBucket bucket = StudentBucket.getInstance();
        List<Student> temp = new ArrayList<>();
        while(true) {
            try {
                temp.add(bucket.generateStudent());
            } catch (StudentsOutOfStockException ex) {
                break;
            }
        }
        assertTrue(gm.checkEndGame());
    }


    @Test
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

    @Test
    void checkTower() {
    }

    @Test
    void checkNeighbourIslands() {
    }

    @Test
    void modifyCostOfCharacter() {
    }

    @Test
    void addNoEntry() {
    }

    @Test
    void evaluateInfluence() {
    }

    @Test
    void setPostmanMovements() {
    }

    @Test
    void thiefEffect() {
    }

    @Test
    void testMoveStudents() {
    }

    @Test
    void setInfluenceCharacter() {
    }
}
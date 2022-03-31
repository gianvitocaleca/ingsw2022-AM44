package it.polimi.ingsw;

import it.polimi.ingsw.model.GameModel;
import it.polimi.ingsw.model.enums.*;
import it.polimi.ingsw.model.player.*;
import it.polimi.ingsw.model.studentcontainers.Cloud;
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
    void establishRoundOrder() {
    }

    @Test
    void moveStudents() {
    }

    @Test
    void moveMotherNature() {
    }

    @Test
    void checkEndGame() {
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
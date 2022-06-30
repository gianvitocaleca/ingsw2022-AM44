package it.polimi.ingsw.evaluatorsTest;

import it.polimi.ingsw.model.exceptions.UnplayableEffectException;
import it.polimi.ingsw.model.exceptions.GameEndedException;
import it.polimi.ingsw.network.server.networkMessages.payloads.CharactersParametersPayload;
import it.polimi.ingsw.model.GameModel;
import it.polimi.ingsw.model.characters.BehaviorCharacter;
import it.polimi.ingsw.model.characters.Character;
import it.polimi.ingsw.model.enums.Color;
import it.polimi.ingsw.model.enums.Creature;
import it.polimi.ingsw.model.enums.Name;
import it.polimi.ingsw.model.enums.Wizard;
import it.polimi.ingsw.model.exceptions.StudentsOutOfStockException;
import it.polimi.ingsw.model.gameboard.MotherNature;
import it.polimi.ingsw.model.gameboard.Table;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.player.Professor;
import it.polimi.ingsw.model.studentcontainers.Island;
import it.polimi.ingsw.model.students.Student;
import it.polimi.ingsw.model.students.StudentBucket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * This class tests the CentaurEvaluator class
 */
public class CentaurEvaluatorTest {
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
                new ArrayList<>(Arrays.asList(Wizard.GANDALF, Wizard.SABRINA, Wizard.BALJEET)));
    }

    /**
     * This tests that when a Centaur Character is played, the influenceEvaluator() method skips the towers
     * when calculating the influence for every player
     */
    @Test
    void centaurEvaluatorTest() throws GameEndedException, UnplayableEffectException {
        int yellowCounter = 0, redCounter = 0, blueCounter = 0, greenCounter = 0, pinkCounter = 0;
        StudentBucket bucket = gm.getBucket();

        Character centaur = new BehaviorCharacter(Name.CENTAUR, gm);
        centaur.effect(new CharactersParametersPayload(
                new ArrayList<>(), 0, 0, new ArrayList<>()));

        Table table = gm.getTable();
        List<Island> islands = table.getIslands();
        try {
            int i = 10;
            do {
                islands.get(0).addStudent(bucket.generateStudent());
                i--;
            } while (i > 0);

        } catch (StudentsOutOfStockException ignored) {

        }
        islands.get(0).setColorOfTowers(Color.BLACK);
        table.setIslands(islands);
        gm.setTable(table);
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

        List<Player> players = gm.getPlayers();

        players.get(0).addProfessor(new Professor(Creature.YELLOW_GNOMES));
        players.get(0).addProfessor(new Professor(Creature.RED_DRAGONS));

        players.get(1).addProfessor(new Professor(Creature.GREEN_FROGS));
        players.get(1).addProfessor(new Professor(Creature.BLUE_UNICORNS));

        players.get(2).addProfessor(new Professor(Creature.PINK_FAIRIES));

        gm.setPlayers(players);

        table = gm.getTable();
        MotherNature mn = table.getMotherNature();
        mn.setCurrentIsland(0);
        table.setMotherNature(mn);
        gm.setTable(table);

        gm.evaluateInfluence();

        if (yellowCounter + redCounter > greenCounter + blueCounter && yellowCounter + redCounter > pinkCounter) {
            assertTrue(gm.getTable().getIslands().get(0).getColorOfTowers().equals(gm.getPlayers().get(0).getMyColor()));
        } else if (greenCounter + blueCounter > yellowCounter + redCounter && greenCounter + blueCounter > pinkCounter) {
            assertTrue(gm.getTable().getIslands().get(0).getColorOfTowers().equals(gm.getPlayers().get(1).getMyColor()));
        } else if (pinkCounter > yellowCounter + redCounter && greenCounter + blueCounter < pinkCounter) {
            assertTrue(gm.getTable().getIslands().get(0).getColorOfTowers().equals(gm.getPlayers().get(2).getMyColor()));
        }
    }
}

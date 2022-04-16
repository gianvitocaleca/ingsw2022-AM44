package it.polimi.ingsw.characterTests;

import it.polimi.ingsw.model.GameModel;
import it.polimi.ingsw.model.characters.CharactersParameters;
import it.polimi.ingsw.model.characters.Thief;
import it.polimi.ingsw.model.enums.Color;
import it.polimi.ingsw.model.enums.Creature;
import it.polimi.ingsw.model.enums.Name;
import it.polimi.ingsw.model.enums.Wizard;
import it.polimi.ingsw.model.exceptions.StudentsOutOfStockException;
import it.polimi.ingsw.model.students.StudentBucket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ThiefTest {

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
     * Removes the students from the dining room of the players
     * Checks if the student bucket correctly updated
     */
    @Test
    void thiefEffectTest() {
        StudentBucket sb = gm.getBucket();
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
}
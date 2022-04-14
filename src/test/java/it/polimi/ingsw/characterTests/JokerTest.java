package it.polimi.ingsw.characterTests;

import it.polimi.ingsw.model.GameModel;
import it.polimi.ingsw.model.characters.CharactersParameters;
import it.polimi.ingsw.model.characters.MoverCharacter;
import it.polimi.ingsw.model.enums.Color;
import it.polimi.ingsw.model.enums.Creature;
import it.polimi.ingsw.model.enums.Name;
import it.polimi.ingsw.model.enums.Wizard;
import it.polimi.ingsw.model.exceptions.StudentsOutOfStockException;
import it.polimi.ingsw.model.students.Student;
import it.polimi.ingsw.model.students.StudentBucket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JokerTest {

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
     * Swaps students between Joker character and player entrance
     */
    @Test
    void JokerTest() {
        int maxStudentsInJoker = 6;
        //create the MoverCharacter
        MoverCharacter joker = new MoverCharacter(Name.JOKER, gm, gm.getTable().getJoker());
        StudentBucket bucket = gm.getBucket();

        //put the character in first position
        gm.getCharacters().remove(0);
        gm.getCharacters().add(0, joker);
        gm.populateMoverCharacter();
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
                gm.getPlayers().get(gm.getCurrentPlayerIndex()).getEntrance().addStudent(bucket.generateStudent());
            } catch (StudentsOutOfStockException ignore) {
            }
        }
        gm.setBucket(bucket);

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
        assertEquals(maxStudentsInJoker, joker.getStudents().size());
        assertEquals(maxStudentsInJoker, gm.getPlayers().get(gm.getCurrentPlayerIndex()).getEntrance().getStudents().size());

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
        assertTrue(oldJokerCreatures.containsAll(newEntranceCreatures));
        assertTrue(oldEntranceCreatures.containsAll(newJokerCreatures));
    }
}



package it.polimi.ingsw.characterTests;

import it.polimi.ingsw.model.GameModel;
import it.polimi.ingsw.model.characters.CharactersParameters;
import it.polimi.ingsw.model.characters.Minstrel;
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

public class MinstrelTest {

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
     * Swaps students between current player entrance and dining room
     */
    @Test
    void minstrelTest() {
        //create the Character
        Minstrel minstrel = new Minstrel(Name.MINSTREL, gm);
        StudentBucket bucket = gm.getBucket();
        //put the character in first position
        gm.getCharacters().remove(0);
        gm.getCharacters().add(0, minstrel);
        //play the first character
        gm.getPlayers().get(gm.getCurrentPlayerIndex()).addCoin();
        gm.playCharacter(0);

        int maxNumberOfStudentsToSwap = 2;
        //populate the current player entrance with random students
        for (int i = 0; i < maxNumberOfStudentsToSwap; i++) {
            try {
                gm.getPlayers().get(gm.getCurrentPlayerIndex()).getEntrance().addStudent(bucket.generateStudent());
            } catch (StudentsOutOfStockException ignore) {
            }
        }
        //populate the current player dining room with random students
        for (int i = 0; i < maxNumberOfStudentsToSwap; i++) {
            try {
                gm.getPlayers().get(gm.getCurrentPlayerIndex()).getDiningRoom().addStudent(bucket.generateStudent());
            } catch (StudentsOutOfStockException ignore) {
            }
        }
        gm.setBucket(bucket);
        //old students in entrance
        List<Student> studentsInEntrance = gm.getPlayers().get(gm.getCurrentPlayerIndex()).getEntrance().getStudents();
        List<Creature> oldEntranceCreatures = new ArrayList<>();
        for (Student s : studentsInEntrance) {
            oldEntranceCreatures.add(s.getCreature());
        }

        //old students in dining room
        List<Student> studentsInDiningRoom = gm.getPlayers().get(gm.getCurrentPlayerIndex()).getDiningRoom().getStudents();
        List<Creature> oldDiningRoomCreatures = new ArrayList<>();
        for (Student s : studentsInDiningRoom) {
            oldDiningRoomCreatures.add(s.getCreature());
        }

        //creates the parameters for the character effect
        CharactersParameters minstrelParameters = new CharactersParameters(oldEntranceCreatures,
                0, 0, null, oldDiningRoomCreatures);
        //play character effect
        gm.effect(minstrelParameters);

        //the number of students should be the same as before
        assertEquals(maxNumberOfStudentsToSwap, gm.getPlayers().get(gm.getCurrentPlayerIndex()).getDiningRoom().getStudents().size());
        assertEquals(maxNumberOfStudentsToSwap, gm.getPlayers().get(gm.getCurrentPlayerIndex()).getEntrance().getStudents().size());

        //get the new creatures in the dining room and the entrance
        List<Creature> newDiningRoomCreatures = new ArrayList<>();
        for (Student s : gm.getPlayers().get(gm.getCurrentPlayerIndex()).getDiningRoom().getStudents()) {
            newDiningRoomCreatures.add(s.getCreature());
        }
        List<Creature> newEntranceCreatures = new ArrayList<>();
        for (Student s : gm.getPlayers().get(gm.getCurrentPlayerIndex()).getEntrance().getStudents()) {
            newEntranceCreatures.add(s.getCreature());
        }

        //the creatures should be swapped
        assertTrue(newEntranceCreatures.containsAll(oldDiningRoomCreatures));
        assertTrue(newDiningRoomCreatures.containsAll(oldEntranceCreatures));


    }
}

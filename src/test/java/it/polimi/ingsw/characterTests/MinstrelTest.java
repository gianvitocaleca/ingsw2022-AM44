package it.polimi.ingsw.characterTests;

import it.polimi.ingsw.model.exceptions.GameEndedException;
import it.polimi.ingsw.server.model.GameModel;
import it.polimi.ingsw.server.networkMessages.CharactersParametersPayload;
import it.polimi.ingsw.server.model.characters.MoverCharacter;
import it.polimi.ingsw.server.model.enums.Color;
import it.polimi.ingsw.server.model.enums.Creature;
import it.polimi.ingsw.server.model.enums.Name;
import it.polimi.ingsw.server.model.enums.Wizard;
import it.polimi.ingsw.server.model.exceptions.StudentsOutOfStockException;
import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.server.model.studentcontainers.DiningRoom;
import it.polimi.ingsw.server.model.students.Student;
import it.polimi.ingsw.server.model.students.StudentBucket;
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
                new ArrayList<>(Arrays.asList(Wizard.GANDALF, Wizard.SABRINA, Wizard.BALJEET)));
    }

    /**
     * Swaps students between current player entrance and dining room
     */
    @Test
    void minstrelTest() throws GameEndedException {
        //create the Character
        MoverCharacter minstrel = new MoverCharacter(Name.MINSTREL, gm);
        StudentBucket bucket = gm.getBucket();
        //put the character in first position
        gm.getCharacters().remove(0);
        gm.getCharacters().add(0, minstrel);
        //play the first character
        List<Player> players = gm.getPlayers();
        players.get(gm.getCurrentPlayerIndex()).addCoin();
        gm.setPlayers(players);
        gm.playCharacter(0);

        int maxNumberOfStudentsToSwap = 2;


        //populate the current player dining room with random students
        for (int i = 0; i < maxNumberOfStudentsToSwap; i++) {
            try {

                players = gm.getPlayers();

                DiningRoom d = players.get(gm.getCurrentPlayerIndex()).getDiningRoom();
                d.addStudent(bucket.generateStudent());
                players.get(gm.getCurrentPlayerIndex()).setDiningRoom(d);

                gm.setPlayers(players);


            } catch (StudentsOutOfStockException ignore) {
            }
        }
        gm.setBucket(bucket);
        //old students in entrance
        List<Student> studentsInEntrance = gm.getPlayers().get(gm.getCurrentPlayerIndex()).getEntrance().getStudents();
        List<Creature> oldEntranceCreatures = new ArrayList<>();
        for (int i = 0; i < maxNumberOfStudentsToSwap; i++) {
            Student s = studentsInEntrance.get(i);
            oldEntranceCreatures.add(s.getCreature());
        }

        //old students in dining room
        List<Student> studentsInDiningRoom = gm.getPlayers().get(gm.getCurrentPlayerIndex()).getDiningRoom().getStudents();
        List<Creature> oldDiningRoomCreatures = new ArrayList<>();
        for (int i = 0; i < maxNumberOfStudentsToSwap; i++) {
            Student s = studentsInDiningRoom.get(i);
            oldDiningRoomCreatures.add(s.getCreature());
        }

        int oldCounter = 0;
        for (Creature c : Creature.values()) {
            oldCounter += gm.getPlayers().get(gm.getCurrentPlayerIndex()).getDiningRoom().getNumberOfStudentsByCreature(c);
        }

        //creates the parameters for the character effect
        CharactersParametersPayload minstrelParameters = new CharactersParametersPayload(oldEntranceCreatures,
                0, 0, oldDiningRoomCreatures);
        //play character effect
        gm.effect(minstrelParameters);

        //the number of students should be the same as before
        int newCounter = 0;
        for (Creature c : Creature.values()) {
            newCounter += gm.getPlayers().get(gm.getCurrentPlayerIndex()).getDiningRoom().getNumberOfStudentsByCreature(c);
        }

        assertEquals(oldCounter, newCounter);

        assertEquals(gm.getPlayers().get(gm.getCurrentPlayerIndex()).getEntrance().getCapacity(),
                gm.getPlayers().get(gm.getCurrentPlayerIndex()).getEntrance().getStudents().size());


        //the creatures should be swapped
        assertEquals(gm.getPlayers().get(gm.getCurrentPlayerIndex()).getEntrance().getStudents().get(7).getCreature(), oldDiningRoomCreatures.get(0));
        assertEquals(gm.getPlayers().get(gm.getCurrentPlayerIndex()).getEntrance().getStudents().get(8).getCreature(), oldDiningRoomCreatures.get(1));


    }
}

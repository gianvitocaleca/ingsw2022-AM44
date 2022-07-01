package it.polimi.ingsw.characterTests;

import it.polimi.ingsw.model.exceptions.UnplayableEffectException;
import it.polimi.ingsw.model.characters.MoverCharacter;
import it.polimi.ingsw.model.exceptions.GameEndedException;
import it.polimi.ingsw.model.GameModel;
import it.polimi.ingsw.network.server.networkMessages.payloads.CharactersParametersPayload;
import it.polimi.ingsw.model.enums.Color;
import it.polimi.ingsw.model.enums.Creature;
import it.polimi.ingsw.model.enums.Name;
import it.polimi.ingsw.model.enums.Wizard;
import it.polimi.ingsw.model.exceptions.StudentsOutOfStockException;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.studentcontainers.DiningRoom;
import it.polimi.ingsw.model.students.Student;
import it.polimi.ingsw.model.students.StudentBucket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
/**
 * This tests the Minstrel character
 */
public class MinstrelTest {

    private GameModel gm;
    private final int characterToPlayIndex = 0;
    private final int maxNumberOfStudentsToSwap = 2;


    /**
     * This creates a new GameModel instance to use in every test
     */
    @BeforeEach
    public void createGameModel() {
        gm = new GameModel(true,
                new ArrayList<>(Arrays.asList("Paolo", "Gianvito", "Sabrina")),
                3,
                new ArrayList<>(Arrays.asList(Color.values())),
                new ArrayList<>(Arrays.asList(Wizard.GANDALF, Wizard.SABRINA, Wizard.BALJEET)));

        gm.getCharacters().remove(characterToPlayIndex);
        gm.getCharacters().add(characterToPlayIndex, new MoverCharacter(Name.MINSTREL, gm, 0));

        List<Player> players = gm.getPlayers();
        Player currPlayer = players.get(gm.getCurrentPlayerIndex());
        //give coins to the current player in order to play the character
        for (int i = 0; i < Name.MINSTREL.getCost(); i++) {
            currPlayer.addCoin();
        }

        gm.setPlayers(players);
    }

    /**
     * Swaps students between current player entrance and dining room
     */
    @Test
    void minstrelTest() throws GameEndedException, UnplayableEffectException {

        List<Player> players;
        StudentBucket bucket = gm.getBucket();

        gm.playCharacter(characterToPlayIndex);

        //populate the current player dining room with random students
        players = gm.getPlayers();
        DiningRoom d = players.get(gm.getCurrentPlayerIndex()).getDiningRoom();

        for (int i = 0; i < maxNumberOfStudentsToSwap; i++) {
            try {
                d.addStudent(bucket.generateStudent());
            } catch (StudentsOutOfStockException ignore) {
            }
        }

        players.get(gm.getCurrentPlayerIndex()).setDiningRoom(d);
        gm.setPlayers(players);
        gm.setBucket(bucket);

        //old students in entrance
        List<Student> studentsInEntrance = gm.getPlayers().get(gm.getCurrentPlayerIndex()).getEntrance().getStudents();
        List<Creature> oldEntranceCreatures = new ArrayList<>();
        for (int i = 0; i < maxNumberOfStudentsToSwap; i++) {
            oldEntranceCreatures.add(studentsInEntrance.get(i).getCreature());
        }

        //old students in dining room
        List<Student> studentsInDiningRoom = gm.getPlayers().get(gm.getCurrentPlayerIndex()).getDiningRoom().getStudents();
        List<Creature> oldDiningRoomCreatures = new ArrayList<>();
        for (int i = 0; i < maxNumberOfStudentsToSwap; i++) {
            oldDiningRoomCreatures.add(studentsInDiningRoom.get(i).getCreature());
        }

        int oldCounter = 0;
        for (Creature c : Creature.values()) {
            oldCounter += gm.getPlayers().get(gm.getCurrentPlayerIndex()).getDiningRoom().getNumberOfStudentsByCreature(c);
        }

        //creates the parameters for the character effect
        CharactersParametersPayload minstrelParameters = new CharactersParametersPayload(oldDiningRoomCreatures,
                0, 0, oldEntranceCreatures);
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

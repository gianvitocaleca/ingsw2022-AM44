package it.polimi.ingsw.characterTests;

import it.polimi.ingsw.model.exceptions.UnplayableEffectException;
import it.polimi.ingsw.model.characters.MoverCharacter;
import it.polimi.ingsw.model.exceptions.GameEndedException;
import it.polimi.ingsw.model.studentcontainers.StudentContainer;
import it.polimi.ingsw.network.server.networkMessages.payloads.CharactersParametersPayload;
import it.polimi.ingsw.model.GameModel;
import it.polimi.ingsw.model.enums.Color;
import it.polimi.ingsw.model.enums.Creature;
import it.polimi.ingsw.model.enums.Name;
import it.polimi.ingsw.model.enums.Wizard;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.students.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * This class tests the Princess character
 */
public class PrincessTest {

    private GameModel gm;
    private final int characterToPlayIndex = 0;
    private final int PRINCESS_CAPACITY = 4;


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
        gm.getCharacters().add(characterToPlayIndex, new MoverCharacter(Name.PRINCESS, gm, PRINCESS_CAPACITY));

        List<Player> players = gm.getPlayers();
        Player currPlayer = players.get(gm.getCurrentPlayerIndex());
        //give coins to the current player in order to play the character
        for (int i = 0; i < Name.PRINCESS.getCost(); i++) {
            currPlayer.addCoin();
        }

        gm.setPlayers(players);
    }

    /**
     * This tests that the princess effect works correctly swapping students between source and destination
     */
    @Test
    void princessEffectTest() throws GameEndedException, UnplayableEffectException {
        gm.playCharacter(characterToPlayIndex);


        //necessary students and creatures from the character and the entrance
        List<Student> studentsInPrincess = ((StudentContainer) gm.getCharacters().get(characterToPlayIndex)).getStudents();
        List<Creature> oldPrincessCreatures = new ArrayList<>();
        for (Student s : studentsInPrincess) {
            oldPrincessCreatures.add(s.getCreature());
        }
        List<Creature> studentToRemoveFromPrincess = new ArrayList<>();
        studentToRemoveFromPrincess.add(oldPrincessCreatures.get(0));
        List<Creature> creaturesInPrincessAfterRemoval = new ArrayList<>();
        for (int i = 1; i < PRINCESS_CAPACITY; i++) {
            creaturesInPrincessAfterRemoval.add(oldPrincessCreatures.get(i));
        }


        List<Student> studentsInDiningRoom = gm.getPlayers().get(gm.getCurrentPlayerIndex()).getDiningRoom().getStudents();
        List<Creature> oldDiningRoomCreatures = new ArrayList<>();
        for (Student s : studentsInDiningRoom) {
            oldDiningRoomCreatures.add(s.getCreature());
        }
        //creates the parameters for the character effect
        CharactersParametersPayload princessParameters = new CharactersParametersPayload(studentToRemoveFromPrincess,
                0, 0, null);
        //play character effect
        gm.effect(princessParameters);
        //the number of students should be the same as before
        assertEquals(PRINCESS_CAPACITY, ((StudentContainer) gm.getCharacters().get(characterToPlayIndex)).getStudents().size());
        //the number of students should increase by one
        assertEquals(oldDiningRoomCreatures.size() + studentToRemoveFromPrincess.size(),
                gm.getPlayers().get(gm.getCurrentPlayerIndex()).getDiningRoom().getStudents().size());

        //get the new creatures in the character and the dining room
        List<Creature> newPrincessCreatures = new ArrayList<>();
        for (Student s : ((StudentContainer) gm.getCharacters().get(characterToPlayIndex)).getStudents()) {
            newPrincessCreatures.add(s.getCreature());
        }
        List<Creature> newDiningRoomCreatures = new ArrayList<>();
        for (Student s : gm.getPlayers().get(gm.getCurrentPlayerIndex()).getDiningRoom().getStudents()) {
            newDiningRoomCreatures.add(s.getCreature());
        }
        //should have moved the student to the dining room
        assertTrue(newDiningRoomCreatures.containsAll(studentToRemoveFromPrincess));
        //should have all the other students in the character
        assertTrue(newPrincessCreatures.containsAll(creaturesInPrincessAfterRemoval));

    }
}

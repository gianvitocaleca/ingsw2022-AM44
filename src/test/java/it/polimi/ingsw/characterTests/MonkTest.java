package it.polimi.ingsw.characterTests;

import it.polimi.ingsw.server.model.exceptions.UnplayableEffectException;
import it.polimi.ingsw.server.model.characters.MoverCharacter;
import it.polimi.ingsw.server.model.exceptions.GameEndedException;
import it.polimi.ingsw.server.model.studentcontainers.StudentContainer;
import it.polimi.ingsw.server.networkMessages.payloads.CharactersParametersPayload;
import it.polimi.ingsw.server.model.GameModel;
import it.polimi.ingsw.server.model.enums.Color;
import it.polimi.ingsw.server.model.enums.Creature;
import it.polimi.ingsw.server.model.enums.Name;
import it.polimi.ingsw.server.model.enums.Wizard;
import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.server.model.studentcontainers.Island;
import it.polimi.ingsw.server.model.students.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MonkTest {

    private GameModel gm;
    private final int characterToPlayIndex = 0;
    private final int MONK_CAPACITY = 4;
    private final int islandDestinationIndex = 1;


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

        gm.getCharacters().remove(characterToPlayIndex);
        gm.getCharacters().add(characterToPlayIndex, new MoverCharacter(Name.MONK,gm,MONK_CAPACITY));

        List<Player> players = gm.getPlayers();
        Player currPlayer = players.get(gm.getCurrentPlayerIndex());
        //give coins to the current player in order to play the character
        for (int i = 0; i < Name.MONK.getCost(); i++) {
            currPlayer.addCoin();
        }

        gm.setPlayers(players);
    }

    /**
     * This tests that the monk effect is correctly executed, swapping the correct students between source and destination
     */
    @Test
    void monkEffectTest() throws GameEndedException, UnplayableEffectException {

        gm.playCharacter(characterToPlayIndex);

        //old islands
        List<Island> oldIslands = gm.getTable().getIslands();

        //necessary students and creatures from the character and the island
        List<Student> studentsInMonk = ((StudentContainer)gm.getCharacters().get(characterToPlayIndex)).getStudents();
        List<Creature> oldMonkCreatures = new ArrayList<>();
        for (Student s : studentsInMonk) {
            oldMonkCreatures.add(s.getCreature());
        }

        List<Creature> studentToRemoveFromMonk = new ArrayList<>();
        studentToRemoveFromMonk.add(oldMonkCreatures.get(0));
        List<Creature> creaturesInMonkAfterRemoval = new ArrayList<>();
        for (int i = 1; i < MONK_CAPACITY; i++) {
            creaturesInMonkAfterRemoval.add(oldMonkCreatures.get(i));
        }

        List<Student> studentsInIslandDestination = gm.getTable().getIslands().get(islandDestinationIndex).getStudents();
        List<Creature> oldIslandCreatures = new ArrayList<>();
        for (Student s : studentsInIslandDestination) {
            oldIslandCreatures.add(s.getCreature());
        }
        //creates the parameters for the character effect
        CharactersParametersPayload monkParameters = new CharactersParametersPayload(studentToRemoveFromMonk,
                islandDestinationIndex, 0, null);
        //play character effect
        gm.effect(monkParameters);
        //the number of students should be the same as before
        assertEquals(MONK_CAPACITY, ((StudentContainer)gm.getCharacters().get(characterToPlayIndex)).getStudents().size());
        //the number of students should increase by one
        assertEquals(oldIslandCreatures.size() + studentToRemoveFromMonk.size(),
                gm.getTable().getIslands().get(islandDestinationIndex).getStudents().size());

        //get the new creatures in the character and the dining room
        List<Creature> newMonkCreatures = new ArrayList<>();
        for (Student s : ((StudentContainer)gm.getCharacters().get(characterToPlayIndex)).getStudents()) {
            newMonkCreatures.add(s.getCreature());
        }
        List<Creature> newIslandCreatures = new ArrayList<>();
        for (Student s : gm.getTable().getIslands().get(islandDestinationIndex).getStudents()) {
            newIslandCreatures.add(s.getCreature());
        }
        //should have moved the student to the destination island
        assertTrue(newIslandCreatures.containsAll(studentToRemoveFromMonk));
        //should have all the other students in the character
        assertTrue(newMonkCreatures.containsAll(creaturesInMonkAfterRemoval));

        //all the other island should be unchanged
        List<Island> newIslands = gm.getTable().getIslands();
        for (int i = 0; i < newIslands.size(); i++) {
            List<Creature> oldCreatures;
            List<Creature> newCreatures;
            if (i != islandDestinationIndex) {
                oldCreatures = oldIslands.get(i).getStudents().stream().map(Student::getCreature).toList();
                newCreatures = newIslands.get(i).getStudents().stream().map(Student::getCreature).toList();
                assertTrue(oldCreatures.containsAll(newCreatures));
            } else {
                oldCreatures = oldIslands.get(i).getStudents().stream().map(Student::getCreature).toList();
                newCreatures = newIslands.get(i).getStudents().stream().map(Student::getCreature).toList();
                assertTrue(newCreatures.containsAll(oldCreatures) && newCreatures.containsAll(studentToRemoveFromMonk));
            }
        }

    }

}

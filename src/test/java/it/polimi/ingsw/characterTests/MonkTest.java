package it.polimi.ingsw.characterTests;

import it.polimi.ingsw.messages.CharactersParameters;
import it.polimi.ingsw.model.GameModel;
import it.polimi.ingsw.model.characters.Character;
import it.polimi.ingsw.model.characters.ConcreteCharacterCreator;
import it.polimi.ingsw.model.enums.Color;
import it.polimi.ingsw.model.enums.Creature;
import it.polimi.ingsw.model.enums.Name;
import it.polimi.ingsw.model.enums.Wizard;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.studentcontainers.Island;
import it.polimi.ingsw.model.students.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MonkTest {

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

    @Test
    void monkEffectTest() {
        int maxStudentsInMonk = 4;
        int characterToPlayIndex = 0;

        List<Name> chars = gm.getCharacters().stream().map(Character::getName).toList();
        if (!chars.contains(Name.MONK)) {
            //create the character and put it in first position
            gm.getCharacters().remove(characterToPlayIndex);
            gm.getCharacters().add(characterToPlayIndex, new ConcreteCharacterCreator().createCharacter(Name.MONK, gm));
            gm.populateMoverCharacter();
        } else {
            for (int i = 0; i < gm.getCharacters().size(); i++) {
                if (gm.getCharacters().get(i).getName().equals(Name.MONK)) {
                    characterToPlayIndex = i;
                }
            }
        }

        List<Player> players = gm.getPlayers();
        Player currPlayer = players.get(gm.getCurrentPlayerIndex());
        //give coins to the current player in order to play the character
        for (int i = 0; i < Name.MONK.getCost(); i++) {
            currPlayer.addCoin();
        }

        gm.setPlayers(players);

        gm.playCharacter(characterToPlayIndex);

        //old islands
        List<Island> oldIslands = gm.getTable().getIslands();

        //necessary students and creatures from the character and the island
        List<Student> studentsInMonk = gm.getTable().getMonk().getStudents();
        List<Creature> oldMonkCreatures = new ArrayList<>();
        for (Student s : studentsInMonk) {
            oldMonkCreatures.add(s.getCreature());
        }
        List<Creature> studentToRemoveFromMonk = new ArrayList<>();
        studentToRemoveFromMonk.add(oldMonkCreatures.get(0));
        List<Creature> creaturesInMonkAfterRemoval = new ArrayList<>();
        for (int i = 1; i < maxStudentsInMonk; i++) {
            creaturesInMonkAfterRemoval.add(oldMonkCreatures.get(i));
        }

        //index of the island to put the student
        int islandDestinationIndex = 1;

        List<Student> studentsInIslandDestination = gm.getTable().getIslands().get(islandDestinationIndex).getStudents();
        List<Creature> oldIslandCreatures = new ArrayList<>();
        for (Student s : studentsInIslandDestination) {
            oldIslandCreatures.add(s.getCreature());
        }
        //creates the parameters for the character effect
        CharactersParameters monkParameters = new CharactersParameters(studentToRemoveFromMonk,
                islandDestinationIndex, 0, null, null);
        //play character effect
        gm.effect(monkParameters);
        //the number of students should be the same as before
        assertEquals(maxStudentsInMonk, gm.getTable().getMonk().getStudents().size());
        //the number of students should increase by one
        assertEquals(oldIslandCreatures.size() + studentToRemoveFromMonk.size(),
                gm.getTable().getIslands().get(islandDestinationIndex).getStudents().size());

        //get the new creatures in the character and the dining room
        List<Creature> newMonkCreatures = new ArrayList<>();
        for (Student s : gm.getTable().getMonk().getStudents()) {
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
            if (i != islandDestinationIndex) {
                List<Creature> oldCreatures;
                List<Creature> newCreatures;
                oldCreatures = oldIslands.get(i).getStudents().stream().map(Student::getCreature).toList();
                newCreatures = newIslands.get(i).getStudents().stream().map(Student::getCreature).toList();
                assertTrue(oldCreatures.containsAll(newCreatures));
            }
        }

    }

}

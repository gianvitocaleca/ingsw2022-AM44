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
import it.polimi.ingsw.model.students.Student;
import it.polimi.ingsw.model.students.StudentBucket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PrincessTest {

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
    void princessEffectTest() {
        int maxStudentsInPrincess = 4;
        int characterToPlayIndex = 0;

        List<Name> chars = gm.getCharacters().stream().map(Character::getName).toList();
        if (!chars.contains(Name.PRINCESS)) {
            //create the character and put it in first position
            gm.getCharacters().remove(characterToPlayIndex);
            gm.getCharacters().add(characterToPlayIndex, new ConcreteCharacterCreator().createCharacter(Name.PRINCESS, gm));
            gm.populateMoverCharacter();
        } else {
            for (int i = 0; i < gm.getCharacters().size(); i++) {
                if (gm.getCharacters().get(i).getName().equals(Name.PRINCESS)) {
                    characterToPlayIndex = i;
                }
            }
        }

        List<Player> players = gm.getPlayers();
        Player currPlayer = players.get(gm.getCurrentPlayerIndex());
        //give coins to the current player in order to play the character
        for (int i = 0; i < Name.PRINCESS.getCost(); i++) {
            currPlayer.addCoin();
        }

        gm.setPlayers(players);

        gm.playCharacter(characterToPlayIndex);


        //necessary students and creatures from the character and the entrance
        List<Student> studentsInPrincess = gm.getTable().getPrincess().getStudents();
        List<Creature> oldPrincessCreatures = new ArrayList<>();
        for (Student s : studentsInPrincess) {
            oldPrincessCreatures.add(s.getCreature());
        }
        List<Creature> studentToRemoveFromPrincess = new ArrayList<>();
        studentToRemoveFromPrincess.add(oldPrincessCreatures.get(0));
        List<Creature> creaturesInPrincessAfterRemoval = new ArrayList<>();
        for (int i = 1; i < maxStudentsInPrincess; i++) {
            creaturesInPrincessAfterRemoval.add(oldPrincessCreatures.get(i));
        }


        List<Student> studentsInDiningRoom = gm.getPlayers().get(gm.getCurrentPlayerIndex()).getDiningRoom().getStudents();
        List<Creature> oldDiningRoomCreatures = new ArrayList<>();
        for (Student s : studentsInDiningRoom) {
            oldDiningRoomCreatures.add(s.getCreature());
        }
        //creates the parameters for the character effect
        CharactersParameters princessParameters = new CharactersParameters(studentToRemoveFromPrincess,
                0, 0, null, null);
        //play character effect
        gm.effect(princessParameters);
        //the number of students should be the same as before
        assertEquals(maxStudentsInPrincess, gm.getTable().getPrincess().getStudents().size());
        //the number of students should increase by one
        assertEquals(oldDiningRoomCreatures.size() + studentToRemoveFromPrincess.size(),
                gm.getPlayers().get(gm.getCurrentPlayerIndex()).getDiningRoom().getStudents().size());

        //get the new creatures in the character and the dining room
        List<Creature> newPrincessCreatures = new ArrayList<>();
        for (Student s : gm.getTable().getPrincess().getStudents()) {
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

package it.polimi.ingsw.characterTests;

import it.polimi.ingsw.model.GameModel;
import it.polimi.ingsw.model.characters.Character;
import it.polimi.ingsw.messages.CharactersParameters;
import it.polimi.ingsw.model.characters.ConcreteCharacterCreator;
import it.polimi.ingsw.model.enums.Color;
import it.polimi.ingsw.model.enums.Creature;
import it.polimi.ingsw.model.enums.Name;
import it.polimi.ingsw.model.enums.Wizard;
import it.polimi.ingsw.model.exceptions.StudentsOutOfStockException;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.studentcontainers.Entrance;
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
        StudentBucket bucket = gm.getBucket();
        int characterToPlayIndex = 0;

        List<Name> chars = gm.getCharacters().stream().map(Character::getName).toList();
        if (!chars.contains(Name.JOKER)) {
            //create the character and put it in first position
            gm.getCharacters().remove(characterToPlayIndex);
            gm.getCharacters().add(characterToPlayIndex, new ConcreteCharacterCreator().createCharacter(Name.JOKER, gm));
            gm.populateMoverCharacter();
        } else {
            for (int i = 0; i < gm.getCharacters().size(); i++) {
                if (gm.getCharacters().get(i).getName().equals(Name.JOKER)) {
                    characterToPlayIndex = i;
                }
            }
        }


        List<Player> players = gm.getPlayers();
        Player currPlayer = players.get(gm.getCurrentPlayerIndex());
        //give coins to the current player in order to play the character
        for (int i = 0; i < Name.JOKER.getCost(); i++) {
            currPlayer.addCoin();
        }
        //populate the current player entrance with random students
        Entrance currPlayerEntrance = currPlayer.getEntrance();
        for (int i = 0; i < maxStudentsInJoker; i++) {
            try {
                currPlayerEntrance.addStudent(bucket.generateStudent());
            } catch (StudentsOutOfStockException e) {
                throw new RuntimeException(e);
            }
        }
        currPlayer.setEntrance(currPlayerEntrance);

        gm.setBucket(bucket);

        gm.setPlayers(players);

        gm.playCharacter(characterToPlayIndex);


        //necessary students and creatures from the character and the entrance
        List<Student> studentsInJoker = gm.getTable().getJoker().getStudents();
        List<Creature> oldJokerCreatures = new ArrayList<>();
        for (Student s : studentsInJoker) {
            oldJokerCreatures.add(s.getCreature());
        }

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
        assertEquals(maxStudentsInJoker, gm.getTable().getJoker().getStudents().size());
        assertEquals(maxStudentsInJoker, gm.getPlayers().get(gm.getCurrentPlayerIndex()).getEntrance().getStudents().size());

        //get the new creatures in the character and the entrance
        List<Creature> newJokerCreatures = new ArrayList<>();
        for (Student s : gm.getTable().getJoker().getStudents()) {
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



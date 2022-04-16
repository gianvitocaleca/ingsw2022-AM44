package it.polimi.ingsw.characterTests;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.characters.*;
import it.polimi.ingsw.model.characters.Character;
import it.polimi.ingsw.model.enums.*;
import it.polimi.ingsw.model.exceptions.StudentsOutOfStockException;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.studentcontainers.DiningRoom;
import it.polimi.ingsw.model.students.*;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class MoverCharacterTest {
    private GameModel gm;

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
        //map to record the old dining rooms
        List<DiningRoom> oldDiningRooms = new ArrayList<>();
        int numberOfStudentsByCreature = 9;
        Creature creatureToRemove = Creature.BLUE_UNICORNS;
        //populates the players dining rooms and saves them
        List<Player> players = new ArrayList<>(gm.getPlayers());
        for (int i = 0; i < players.size(); i++) {
            DiningRoom playerDR = new DiningRoom(numberOfStudentsByCreature);
            List<Student> newStudents = new ArrayList<>();
            for (int j = 0; j < 10; j++) {
                try {
                    newStudents.add(sb.generateStudent());
                } catch (StudentsOutOfStockException ignore) {
                }
            }
            playerDR.addStudents(newStudents);
            players.get(i).setDiningRoom(playerDR);
            //gives the necessary coins to the player
            players.get(i).addCoin();
            players.get(i).addCoin();
            players.get(i).addCoin();
            oldDiningRooms.add(playerDR);
        }
        gm.setPlayers(players);
        //creates the necessary parameters for the character
        List<Creature> uni = new ArrayList<>();
        uni.add(creatureToRemove);
        CharactersParameters thief = new CharactersParameters(uni, 0, 0, null, new ArrayList<>());
        //puts the thief as first character
        gm.getCharacters().remove(0);
        gm.getCharacters().add(0, new Thief(Name.THIEF, gm));

        //plays the character
        assertTrue(gm.playCharacter(0));
        assertTrue(gm.effect(thief));

        for (int i = 0; i < gm.getPlayers().size(); i++) {
            for (Creature c : Creature.values()) {
                if (!c.equals(creatureToRemove)) {
                    //should have the same creatures that were not removed
                    assertEquals(oldDiningRooms.get(i).getNumberOfStudentsByCreature(c),
                            gm.getPlayers().get(i).getDiningRoom().getNumberOfStudentsByCreature(c));
                } else if (oldDiningRooms.get(i).getNumberOfStudentsByCreature(c) < 3) {
                    //should have zero creatures
                    assertEquals(0, gm.getPlayers().get(i).getDiningRoom().getNumberOfStudentsByCreature(c));
                } else {
                    //should have old value - 3 creatures
                    assertEquals(oldDiningRooms.get(i).getNumberOfStudentsByCreature(c) - 3,
                            gm.getPlayers().get(i).getDiningRoom().getNumberOfStudentsByCreature(c));
                }
            }
        }
    }

    @Test
    void princessEffectTest() {
        //creates the character
        ConcreteCharacterCreator ccc = new ConcreteCharacterCreator();
        Character princess = ccc.createCharacter(Name.PRINCESS, gm);
        //put the character in first position
        gm.getCharacters().remove(0);
        gm.getCharacters().add(0, princess);
        //give coins to player
        gm.getPlayers().get(gm.getCurrentPlayerIndex()).addCoin();
        gm.getPlayers().get(gm.getCurrentPlayerIndex()).addCoin();
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
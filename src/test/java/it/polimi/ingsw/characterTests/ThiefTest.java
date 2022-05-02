package it.polimi.ingsw.characterTests;

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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ThiefTest {

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
     * Removes the students from the dining room of the players
     * Checks if the student bucket correctly updated
     */
    @Test
    void thiefEffectTest() {
        StudentBucket sb = gm.getBucket();
        //map to record the old dining rooms
        Map<String, DiningRoom> oldDiningRooms = new HashMap<>();
        int numberOfStudentsByCreature = 9;
        Creature creatureToRemove = Creature.BLUE_UNICORNS;
        //populates the players dining rooms and saves them
        List<Player> players = new ArrayList<>(gm.getPlayers());
        for (Player p : players) {
            DiningRoom playerDR = new DiningRoom(numberOfStudentsByCreature);
            List<Student> newStudents = new ArrayList<>();
            for (int j = 0; j < 10; j++) {
                try {
                    newStudents.add(sb.generateStudent());
                } catch (StudentsOutOfStockException ignore) {
                }
            }
            playerDR.addStudents(newStudents);
            p.setDiningRoom(playerDR);
            //gives the necessary coins to the player
            p.addCoin();
            p.addCoin();
            p.addCoin();
            oldDiningRooms.put(p.getUsername(), playerDR);
        }
        gm.setPlayers(players);
        //creates the necessary parameters for the character
        List<Creature> uni = new ArrayList<>();
        uni.add(creatureToRemove);
        CharactersParametersPayload thief = new CharactersParametersPayload(uni, 0, 0, null, new ArrayList<>());
        //puts the thief as first character
        gm.getCharacters().remove(0);
        gm.getCharacters().add(0, new MoverCharacter(Name.THIEF, gm));

        //plays the character
        assertTrue(gm.playCharacter(0));
        assertTrue(gm.effect(thief));

        for (Player p : gm.getPlayers()) {
            for (Creature c : Creature.values()) {
                if (!c.equals(creatureToRemove)) {
                    //should have the same creatures that were not removed
                    assertEquals(oldDiningRooms.get(p.getUsername()).getNumberOfStudentsByCreature(c),
                            p.getDiningRoom().getNumberOfStudentsByCreature(c));
                } else if (oldDiningRooms.get(p.getUsername()).getNumberOfStudentsByCreature(c) < 3) {
                    //should have zero creatures
                    assertEquals(0, p.getDiningRoom().getNumberOfStudentsByCreature(c));
                } else {
                    //should have old value - 3 creatures
                    assertEquals(oldDiningRooms.get(p.getUsername()).getNumberOfStudentsByCreature(c) - 3,
                            p.getDiningRoom().getNumberOfStudentsByCreature(c));
                }
            }
        }
    }
}
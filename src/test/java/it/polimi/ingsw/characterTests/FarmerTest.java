package it.polimi.ingsw.characterTests;

import it.polimi.ingsw.server.model.GameModel;
import it.polimi.ingsw.server.model.enums.Color;
import it.polimi.ingsw.server.model.enums.Creature;
import it.polimi.ingsw.server.model.enums.Wizard;
import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.server.model.studentcontainers.DiningRoom;
import it.polimi.ingsw.server.model.students.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FarmerTest {

    GameModel gm;


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
    }

    /**
     * This tests that when the Farmer character is played, the model uses the correct version of check professor method.
     */
    @Test
    public void checkProfessorFarmerTest(){

        List<Player> players = gm.getPlayers();

        DiningRoom room = new DiningRoom();
        room.addStudent(new Student(Creature.RED_DRAGONS));
        players.get(0).setDiningRoom(room);
        gm.setPlayers(players);
        gm.checkProfessor();


        players = gm.getPlayers();
        room = new DiningRoom();
        room.addStudent(new Student(Creature.RED_DRAGONS));
        room.addStudent(new Student(Creature.RED_DRAGONS));
        players.get(1).setDiningRoom(room);

        gm.setPlayers(players);
        gm.checkProfessor();

        players = gm.getPlayers();
        room = new DiningRoom();
        room.addStudent(new Student(Creature.RED_DRAGONS));
        room.addStudent(new Student(Creature.RED_DRAGONS));
        players.get(0).setDiningRoom(room);

        gm.setPlayers(players);
        gm.setCurrentPlayerIndex(0);
        gm.setFarmer();

        gm.checkProfessor();

        assertEquals(0, gm.getPlayers().get(1).getProfessors().size());
        assertTrue(gm.getPlayers().get(0).getProfessors().size()==1 && gm.getPlayers().get(0).getProfessors().get(0).getCreature().equals(Creature.RED_DRAGONS));
        room = new DiningRoom();
        room.addStudent(new Student(Creature.RED_DRAGONS));
        room.addStudent(new Student(Creature.RED_DRAGONS));
        room.addStudent(new Student(Creature.RED_DRAGONS));
        players.get(1).setDiningRoom(room);
        gm.checkProfessor();
        assertEquals(0, gm.getPlayers().get(0).getProfessors().size());
        assertTrue(gm.getPlayers().get(1).getProfessors().size()==1 && gm.getPlayers().get(1).getProfessors().get(0).getCreature().equals(Creature.RED_DRAGONS));
    }
}

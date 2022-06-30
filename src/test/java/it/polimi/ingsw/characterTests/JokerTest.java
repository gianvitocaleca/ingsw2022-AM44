package it.polimi.ingsw.characterTests;

import it.polimi.ingsw.model.exceptions.UnplayableEffectException;
import it.polimi.ingsw.model.characters.MoverCharacter;
import it.polimi.ingsw.model.exceptions.GameEndedException;
import it.polimi.ingsw.model.GameModel;
import it.polimi.ingsw.model.studentcontainers.StudentContainer;
import it.polimi.ingsw.network.server.networkMessages.payloads.CharactersParametersPayload;
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

import static org.junit.jupiter.api.Assertions.*;

/**
 * This tests the Joker class
 */
public class JokerTest {

    private GameModel gm;
    private final int characterToPlayIndex = 0;
    private final int JOKER_CAPACITY = 6;


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
        gm.getCharacters().add(characterToPlayIndex, new MoverCharacter(Name.JOKER, gm, JOKER_CAPACITY));

        List<Player> players = gm.getPlayers();
        Player currPlayer = players.get(gm.getCurrentPlayerIndex());
        //give coins to the current player in order to play the character
        for (int i = 0; i < Name.JOKER.getCost(); i++) {
            currPlayer.addCoin();
        }

        gm.setPlayers(players);
    }

    /**
     * Swaps students between Joker character and player entrance
     */
    @Test
    void JokerTest() throws GameEndedException, UnplayableEffectException {


        gm.playCharacter(characterToPlayIndex);

        //necessary students and creatures from the character and the entrance
        List<Student> studentsInJoker = ((StudentContainer) gm.getCharacters().get(characterToPlayIndex)).getStudents();
        List<Creature> oldJokerCreatures = new ArrayList<>();
        oldJokerCreatures.add(studentsInJoker.get(0).getCreature());

        List<Student> studentsInEntrance = gm.getPlayers().get(gm.getCurrentPlayerIndex()).getEntrance().getStudents();
        List<Creature> oldEntranceCreatures = new ArrayList<>();
        oldEntranceCreatures.add(studentsInEntrance.get(0).getCreature());


        //creates the parameters for the character effect
        CharactersParametersPayload jokerParameters = new CharactersParametersPayload(oldJokerCreatures,
                0, 0, oldEntranceCreatures);
        //play character effect
        gm.effect(jokerParameters);
        //the number of students should be the same as before
        assertEquals(JOKER_CAPACITY, ((StudentContainer) gm.getCharacters().get(characterToPlayIndex)).getStudents().size());
        assertEquals(gm.getPlayers().get(gm.getCurrentPlayerIndex()).getEntrance().getCapacity(),
                gm.getPlayers().get(gm.getCurrentPlayerIndex()).getEntrance().getStudents().size());

        assertEquals(gm.getPlayers().get(gm.getCurrentPlayerIndex()).getEntrance().getStudents().get(8).getCreature(), oldJokerCreatures.get(0));

        assertEquals(((StudentContainer) gm.getCharacters().get(characterToPlayIndex)).getStudents().get(5).getCreature(), oldEntranceCreatures.get(0));

    }

    @Test
    void WrongJokerTest() throws GameEndedException, UnplayableEffectException {

        gm.playCharacter(characterToPlayIndex);

        //necessary students and creatures from the character and the entrance
        List<Student> studentsInJoker = ((StudentContainer) gm.getCharacters().get(characterToPlayIndex)).getStudents();
        List<Creature> oldJokerCreatures = new ArrayList<>();
        oldJokerCreatures.add(studentsInJoker.get(0).getCreature());
        oldJokerCreatures.add(studentsInJoker.get(1).getCreature());
        oldJokerCreatures.add(studentsInJoker.get(2).getCreature());
        oldJokerCreatures.add(studentsInJoker.get(3).getCreature());

        List<Student> studentsInEntrance = gm.getPlayers().get(gm.getCurrentPlayerIndex()).getEntrance().getStudents();
        List<Creature> oldEntranceCreatures = new ArrayList<>();
        oldEntranceCreatures.add(studentsInEntrance.get(0).getCreature());
        oldEntranceCreatures.add(studentsInEntrance.get(1).getCreature());
        oldEntranceCreatures.add(studentsInEntrance.get(2).getCreature());
        oldEntranceCreatures.add(studentsInEntrance.get(3).getCreature());


        //creates the parameters for the character effect
        CharactersParametersPayload jokerParameters = new CharactersParametersPayload(oldJokerCreatures,
                0, 0, oldEntranceCreatures);
        //play character effect
        assertFalse(gm.effect(jokerParameters));

    }
}
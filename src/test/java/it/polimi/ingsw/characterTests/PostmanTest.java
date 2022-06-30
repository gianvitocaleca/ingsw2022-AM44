package it.polimi.ingsw.characterTests;

import it.polimi.ingsw.model.exceptions.UnplayableEffectException;
import it.polimi.ingsw.model.exceptions.GameEndedException;
import it.polimi.ingsw.model.GameModel;
import it.polimi.ingsw.network.server.networkMessages.payloads.CharactersParametersPayload;
import it.polimi.ingsw.model.characters.Postman;
import it.polimi.ingsw.model.enums.Color;
import it.polimi.ingsw.model.enums.Name;
import it.polimi.ingsw.model.enums.Wizard;
import it.polimi.ingsw.model.exceptions.AssistantAlreadyPlayedException;
import it.polimi.ingsw.model.exceptions.PlanningPhaseEndedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class tests the postman class
 */
public class PostmanTest {
    private GameModel gm;

    /**
     * This method creates a GameModel class that is used in every test
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
     * This tests that when the postman card is played, motherNature moves of jumps+postmanMovements positions
     * @throws GameEndedException
     * @throws UnplayableEffectException
     */
    @Test
    public void postmanTest() throws GameEndedException, UnplayableEffectException {
        int numberOfPostmanJumps = 2;
        CharactersParametersPayload Postman = new CharactersParametersPayload(new ArrayList<>(),
                0, numberOfPostmanJumps, new ArrayList<>());
        try {
            gm.playAssistant(0);
            gm.setCurrentPlayerIndex(0);
        } catch (AssistantAlreadyPlayedException | PlanningPhaseEndedException e) {
            e.printStackTrace();
        }
        gm.getCharacters().remove(0);
        gm.getCharacters().add(0, new Postman(Name.MAGICPOSTMAN, gm));

        gm.getPlayers().get(gm.getCurrentPlayerIndex()).addCoin();
        gm.getPlayers().get(gm.getCurrentPlayerIndex()).addCoin();
        gm.getPlayers().get(gm.getCurrentPlayerIndex()).addCoin();

        gm.playCharacter(0);
        gm.effect(Postman);

        int oldPosition = gm.getTable().getMnPosition();
        int numberOfMNJumps = 1;

        if (2 < ((gm.getTable().getIslands().size() - 1) - gm.getTable().getMnPosition())) {
            try {
                gm.moveMotherNature(numberOfMNJumps);
            } catch (GameEndedException ignore) {

            }
            assertTrue(gm.getTable().getMnPosition() == oldPosition + (numberOfMNJumps + numberOfPostmanJumps));
        } else {
            try {
                gm.moveMotherNature(numberOfMNJumps);
            } catch (GameEndedException ignore) {

            }
            assertTrue(gm.getTable().getMnPosition() == (oldPosition + (numberOfMNJumps + numberOfPostmanJumps)) % (gm.getTable().getIslands().size()));
        }

    }

    /**
     * This tests that when a wrong value for the postmanMovements is provided, the game will
     * not execute the moveMotherNature method
     * @throws GameEndedException
     * @throws UnplayableEffectException
     */
    @Test
    public void setWrongPostmanMovementsTest() throws GameEndedException, UnplayableEffectException {
        CharactersParametersPayload Postman = new CharactersParametersPayload(new ArrayList<>(),
                0, 200, new ArrayList<>());

        gm.getCharacters().remove(0);
        gm.getCharacters().add(0, new Postman(Name.MAGICPOSTMAN, gm));

        gm.getPlayers().get(gm.getCurrentPlayerIndex()).addCoin();
        gm.getPlayers().get(gm.getCurrentPlayerIndex()).addCoin();
        gm.getPlayers().get(gm.getCurrentPlayerIndex()).addCoin();

        gm.playCharacter(0);
        assertFalse(gm.effect(Postman));
    }
}

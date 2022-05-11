package it.polimi.ingsw.characterTests;

import it.polimi.ingsw.server.model.GameModel;
import it.polimi.ingsw.server.networkMessages.CharactersParametersPayload;
import it.polimi.ingsw.server.model.characters.Postman;
import it.polimi.ingsw.server.model.enums.Color;
import it.polimi.ingsw.server.model.enums.Name;
import it.polimi.ingsw.server.model.enums.Wizard;
import it.polimi.ingsw.server.model.exceptions.AssistantAlreadyPlayedException;
import it.polimi.ingsw.server.model.exceptions.PlanningPhaseEndedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;


public class PostmanTest {
    private GameModel gm;

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
     */
    @Test
    public void postmanTest() {
        CharactersParametersPayload Postman = new CharactersParametersPayload(new ArrayList<>(),
                0, 2, null, new ArrayList<>());
        try {
            gm.playAssistant(0);
            gm.setCurrentPlayerIndex(0);
        }catch(AssistantAlreadyPlayedException e){
            e.printStackTrace();
        }catch(PlanningPhaseEndedException e){
            e.printStackTrace();
        }
        gm.getCharacters().remove(0);
        gm.getCharacters().add(0, new Postman(Name.MAGICPOSTMAN, gm));

        gm.getPlayers().get(gm.getCurrentPlayerIndex()).addCoin();
        gm.getPlayers().get(gm.getCurrentPlayerIndex()).addCoin();
        gm.getPlayers().get(gm.getCurrentPlayerIndex()).addCoin();

        gm.playCharacter(0);
        gm.effect(Postman);

        int oldposition = gm.getTable().getMnPosition();

        if (2 < ((gm.getTable().getIslands().size() - 1) - gm.getTable().getMnPosition())) {
            gm.moveMotherNature(0);
            assertTrue(gm.getTable().getMnPosition() == oldposition + 2);
        } else {
            gm.moveMotherNature(0);
            assertTrue(gm.getTable().getMnPosition() == (oldposition + 2) % (gm.getTable().getIslands().size()));
        }

    }

    /**
     * This tests that when a wrong value for the postmanMovements is provided, the game will not execute the moveMotherNature method
     */
    @Test
    public void setWrongPostmanMovementsTest() {
        CharactersParametersPayload Postman = new CharactersParametersPayload(new ArrayList<>(),
                0, 200, null, new ArrayList<>());

        gm.getCharacters().remove(0);
        gm.getCharacters().add(0, new Postman(Name.MAGICPOSTMAN, gm));

        gm.getPlayers().get(gm.getCurrentPlayerIndex()).addCoin();
        gm.getPlayers().get(gm.getCurrentPlayerIndex()).addCoin();
        gm.getPlayers().get(gm.getCurrentPlayerIndex()).addCoin();

        gm.playCharacter(0);
        assertFalse(gm.effect(Postman));
    }
}
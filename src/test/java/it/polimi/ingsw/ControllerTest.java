package it.polimi.ingsw;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.controller.enums.GamePhases;
import it.polimi.ingsw.controller.events.PlanningEvent;
import it.polimi.ingsw.model.GameModel;
import it.polimi.ingsw.model.enums.Color;
import it.polimi.ingsw.model.enums.Wizard;
import it.polimi.ingsw.view.ViewProxy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ControllerTest {

    GameModel gm;
    Controller controller;
    ViewProxy view;


    /**
     * This create a new GameModel instance to use in every test
     */
    @BeforeEach
    public void createGame() {
        gm = new GameModel(true,
                new ArrayList<>(Arrays.asList("Paolo", "Gianvito", "Sabrina")),
                3,
                new ArrayList<>(Arrays.asList(Color.values())),
                new ArrayList<>(Arrays.asList(Wizard.YELLOW, Wizard.PINK, Wizard.BLUE)));

        view = new ViewProxy();
        controller = new Controller(gm,view);
    }

    @Test
    public void playAssistantTest(){

        int pastPlayerIndex = gm.getCurrentPlayerIndex();

        PlanningEvent evt = new PlanningEvent(view,0);
        view.messageReceiver(evt);

        int currPlayerIndex = gm.getCurrentPlayerIndex();

        assertTrue(pastPlayerIndex!=currPlayerIndex);

        assertEquals(9, gm.getPlayers().get(pastPlayerIndex).getAssistantDeck().size());
        assertEquals(currPlayerIndex, (pastPlayerIndex + 1) % (gm.getNumberOfPlayers()));


    }

    @Test
    public void playWrongAssistantTest(){

        int pastPlayerIndex = gm.getCurrentPlayerIndex();

        PlanningEvent evt = new PlanningEvent(view,11);
        view.messageReceiver(evt);

        int currPlayerIndex = gm.getCurrentPlayerIndex();

        assertTrue(pastPlayerIndex==currPlayerIndex);
        assertEquals(10, gm.getPlayers().get(pastPlayerIndex).getAssistantDeck().size());

    }

    @Test
    public void completePlanningPhaseTest(){


        PlanningEvent evt = new PlanningEvent(view,0);
        view.messageReceiver(evt);
        evt = new PlanningEvent(view,1);
        view.messageReceiver(evt);
        evt = new PlanningEvent(view,2);
        view.messageReceiver(evt);


        assertEquals(controller.getCurrentPhase(), GamePhases.ACTION_STUDENTSMOVEMENT);
        assertEquals(gm.getCurrentPlayerIndex(),0);


    }
}

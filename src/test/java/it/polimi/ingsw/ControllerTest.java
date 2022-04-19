package it.polimi.ingsw;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.controller.enums.GamePhases;
import it.polimi.ingsw.controller.events.CharacterParametersEvent;
import it.polimi.ingsw.controller.events.PlanningEvent;
import it.polimi.ingsw.controller.events.PlayCharacterEvent;
import it.polimi.ingsw.messages.CharactersParameters;
import it.polimi.ingsw.model.GameModel;
import it.polimi.ingsw.model.characters.ConcreteCharacterCreator;
import it.polimi.ingsw.model.characters.Postman;
import it.polimi.ingsw.model.enums.Color;
import it.polimi.ingsw.model.enums.Name;
import it.polimi.ingsw.model.enums.Wizard;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.view.ViewProxy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
        view.playAssistantReceiver(evt);

        int currPlayerIndex = gm.getCurrentPlayerIndex();

        assertTrue(pastPlayerIndex!=currPlayerIndex);

        assertEquals(9, gm.getPlayers().get(pastPlayerIndex).getAssistantDeck().size());
        assertEquals(currPlayerIndex, (pastPlayerIndex + 1) % (gm.getNumberOfPlayers()));


    }

    @Test
    public void playWrongAssistantTest(){

        int pastPlayerIndex = gm.getCurrentPlayerIndex();

        PlanningEvent evt = new PlanningEvent(view,11);
        view.playAssistantReceiver(evt);

        int currPlayerIndex = gm.getCurrentPlayerIndex();

        assertTrue(pastPlayerIndex==currPlayerIndex);
        assertEquals(10, gm.getPlayers().get(pastPlayerIndex).getAssistantDeck().size());

    }

    @Test
    public void completePlanningPhaseTest(){


        PlanningEvent evt = new PlanningEvent(view,0);
        view.playAssistantReceiver(evt);
        evt = new PlanningEvent(view,1);
        view.playAssistantReceiver(evt);
        evt = new PlanningEvent(view,2);
        view.playAssistantReceiver(evt);


        assertEquals(controller.getCurrentPhase(), GamePhases.ACTION_STUDENTSMOVEMENT);
        assertEquals(gm.getCurrentPlayerIndex(),0);


    }

    @Test
    public void playCharacterTest(){
        ConcreteCharacterCreator ccc = new ConcreteCharacterCreator();
        gm.getCharacters().set(0, ccc.createCharacter(Name.MAGICPOSTMAN,gm));

        completePlanningPhaseTest();

        PlayCharacterEvent evt = new PlayCharacterEvent(view,0);
        view.playCharacterReceiver(evt);

        assertTrue(controller.isWaitingForParameters());

        CharactersParameters parameters = new CharactersParameters(new ArrayList<>(),0,2,null, new ArrayList<>());
        CharacterParametersEvent ev2 = new CharacterParametersEvent(view,parameters);
        view.characterParametersReceiver(ev2);

        assertEquals(gm.getPostmanMovements(),2);

    }

    @Test
    public void playWrongCharacterTest(){

        completePlanningPhaseTest();

        PlayCharacterEvent evt = new PlayCharacterEvent(view,3);
        view.playCharacterReceiver(evt);

        assertFalse(controller.isWaitingForParameters());

    }

    @Test
    public void playCharacterWithPoorPlayerTest(){
        ConcreteCharacterCreator ccc = new ConcreteCharacterCreator();
        gm.getCharacters().set(0, ccc.createCharacter(Name.MAGICPOSTMAN,gm));

        completePlanningPhaseTest();

        List<Player> temp = gm.getPlayers();
        temp.get(gm.getCurrentPlayerIndex()).removeCoin(1);
        gm.setPlayers(temp);

        PlayCharacterEvent evt = new PlayCharacterEvent(view,0);
        view.playCharacterReceiver(evt);

        assertFalse(controller.isWaitingForParameters());

    }
}

package it.polimi.ingsw.playerTests;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.model.GameModel;
import it.polimi.ingsw.model.enums.Color;
import it.polimi.ingsw.model.enums.Assistants;
import it.polimi.ingsw.model.enums.Wizard;
import it.polimi.ingsw.model.exceptions.AssistantAlreadyPlayedException;
import it.polimi.ingsw.model.exceptions.PlanningPhaseEndedException;
import it.polimi.ingsw.model.player.Assistant;
import org.junit.jupiter.api.*;

import java.util.*;

class AssistantTest {

    private Assistants name;
    GameModel gm;

    /**
     * Creates a random assistant
     */
    @BeforeEach
    public void InitializeAssistant() {
        name = Assistants.values()[new Random().nextInt(Assistants.values().length)];
    }

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
     * This tests that the assistants values initialization is correctly made
     */
    @Test
    void getValue() {
        for (Assistants val : Assistants.values()) {
            if (val.equals(name)) {
                assertEquals(val.getValue(), name.getValue());
            }
        }
    }

    /**
     * This tests that the assistants movements initialization is correctly made
     */
    @Test
    void getMovements() {
        for (Assistants val : Assistants.values()) {
            if (val.equals(name)) {
                assertEquals(val.getMovements(), name.getMovements());
            }
        }
    }

    /**
     * This test verifies the correct behaviour of assistantDeck and lastPlayedCard when every assistant is played.
     * assistantDeck should reduce its length by one, lastPlayedCard should increase its length by one.
     */
    @Test
    void playEveryAssistant() {
        for (int i = 0; i < Assistants.values().length; i++) {
            try {
                gm.playAssistant(0);
            } catch (AssistantAlreadyPlayedException | PlanningPhaseEndedException e) {
                e.printStackTrace();
            }
            gm.setCurrentPlayerIndex(0);
            assertEquals(gm.getPlayers().get(gm.getCurrentPlayerIndex()).getAssistantDeck().size(), 9 - i);
            assertEquals(gm.getPlayers().get(gm.getCurrentPlayerIndex()).getLastPlayedCards().size(), 1 + i);
        }
    }

    /**
     * Verifies the method playAssistant return false when a player gives a wrong index.
     * The method should not change AssistantDeck and lastPlayedCard
     */
    @Test
    void playNotExistentAssistant() {
        for (int i = 0; i < gm.getPlayers().size(); i++) {
            gm.setCurrentPlayerIndex(i);
            try {
                gm.playAssistant(i);
            } catch (AssistantAlreadyPlayedException | PlanningPhaseEndedException ignore) {
            }
        }
        gm.setCurrentPlayerIndex(0);
        Assistant lastPlayed = gm.getPlayers().get(0).getLastPlayedCard();
        try {
            assertFalse(gm.playAssistant(123));
        } catch (AssistantAlreadyPlayedException | PlanningPhaseEndedException ignore) {
        }
        assertEquals(lastPlayed.getValue(), gm.getPlayers().get(0).getLastPlayedCard().getValue());
    }

    /**
     * Verifies that the method allows the first player to play the AssistantCard he prefers,
     * the others cannot play that card and in that case the methods throws an exception.
     */
    @Test
    void playAssistantAlreadyPlayed() {
        for (int i = 0; i < gm.getPlayers().size(); i++) {
            gm.setCurrentPlayerIndex(i); //0
            if (gm.getCurrentPlayerIndex() == 0) {
                try {
                    assertTrue(gm.playAssistant(0));
                    assertEquals(gm.getPlayers().get(0).getAssistantDeck().size(), 9);
                    assertEquals(gm.getPlayers().get(0).getLastPlayedCards().size(), 1);
                } catch (AssistantAlreadyPlayedException | PlanningPhaseEndedException ignore) {
                }
            } else { //o sei 1 o sei 2
                try {
                    gm.playAssistant(0);
                } catch (AssistantAlreadyPlayedException ex) {
                    assertEquals(gm.getPlayers().get(gm.getCurrentPlayerIndex()).getAssistantDeck().size(), 10);
                    assertEquals(gm.getPlayers().get(gm.getCurrentPlayerIndex()).getLastPlayedCards().size(), 0);
                    try {
                        gm.playAssistant(1);
                    } catch (PlanningPhaseEndedException | AssistantAlreadyPlayedException ignore) {
                    }

                } catch (PlanningPhaseEndedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
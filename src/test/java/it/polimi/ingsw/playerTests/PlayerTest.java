package it.polimi.ingsw.playerTests;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.model.enums.Color;
import it.polimi.ingsw.model.enums.Value;
import it.polimi.ingsw.model.enums.Wizard;
import it.polimi.ingsw.model.player.Assistant;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.studentcontainers.Entrance;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class PlayerTest {

    /**
     * This tests that the created player has the provided username
     */
    @Test
    void getUsernameCorrectly() {
        Player p1 = new Player("userName", Color.BLACK,1, Wizard.BLUE,8,new Entrance(7));
        assertEquals(p1.getUsername(),"userName");
    }

    /**
     * This tests that the created player has the provided color
     */
    @Test
    void getMyCorrectColor() {
        Player p1 = new Player("userName",Color.BLACK,1,Wizard.BLUE,8,new Entrance(7));
        assertEquals(p1.getMyColor(),Color.BLACK);
    }

    /**
     * This tests that when an assistant card is played, the lastPlayedCard parameter is correctly set
     */
    @Test
    void getLastPlayedCard() {
        Player p1 = new Player("userName",Color.BLACK,1,Wizard.BLUE,8,new Entrance(7));
        Assistant a = new Assistant(Value.CAT);
        p1.setAssistantCard(a);
        assertEquals(p1.getLastPlayedCard(),a);
        Assistant b = new Assistant(Value.DOG);
        assertFalse(p1.getLastPlayedCard().equals(b));
        p1.setAssistantCard(b);
        assertEquals(p1.getLastPlayedCard(),b);
    }


}
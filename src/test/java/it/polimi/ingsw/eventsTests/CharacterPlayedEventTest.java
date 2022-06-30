package it.polimi.ingsw.eventsTests;

import it.polimi.ingsw.controller.events.CharacterPlayedEvent;
import it.polimi.ingsw.model.enums.Name;
import org.junit.jupiter.api.Test;

import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class tests the correct behavior of CharacterPlayedEvent
 */
public class CharacterPlayedEventTest {
    /**
     * This method tests the correct creation of a CharacterPlayedEvent
     */
    @Test
    public void eventCreation() {
        Socket s = new Socket();
        Name n = Name.MAGICPOSTMAN;
        CharacterPlayedEvent evt = new CharacterPlayedEvent(this, n, s);
        assertEquals(s, evt.getSocket());
        assertEquals(n, evt.getCharactersName());
    }
}

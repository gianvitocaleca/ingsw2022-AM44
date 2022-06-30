package it.polimi.ingsw.eventsTests;

import it.polimi.ingsw.controller.events.CharacterPlayedEvent;
import it.polimi.ingsw.model.enums.Name;
import org.junit.jupiter.api.Test;

import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

public class CharacterPlayedEventTest {
    @Test
    public void eventCreation() {
        Socket s = new Socket();
        Name n = Name.MAGICPOSTMAN;
        CharacterPlayedEvent evt = new CharacterPlayedEvent(this, n, s);
        assertEquals(s, evt.getSocket());
        assertEquals(n, evt.getCharactersName());
    }
}

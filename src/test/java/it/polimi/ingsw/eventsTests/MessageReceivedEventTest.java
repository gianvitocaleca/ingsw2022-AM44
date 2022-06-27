package it.polimi.ingsw.eventsTests;

import it.polimi.ingsw.controller.events.MessageReceivedEvent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MessageReceivedEventTest {

    @Test
    public void eventCreation() {
        String message = "Stringa";
        MessageReceivedEvent evt = new MessageReceivedEvent(this, message);
        assertEquals(message, evt.getMessage());
    }
}

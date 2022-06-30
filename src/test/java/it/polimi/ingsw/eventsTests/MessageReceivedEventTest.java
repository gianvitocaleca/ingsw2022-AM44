package it.polimi.ingsw.eventsTests;

import it.polimi.ingsw.controller.events.MessageReceivedEvent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class tests the message received event class
 */
public class MessageReceivedEventTest {

    /**
     * This tests the correct creation of a MessageReceivedEvent
     */
    @Test
    public void eventCreation() {
        String message = "String";
        MessageReceivedEvent evt = new MessageReceivedEvent(this, message);
        assertEquals(message, evt.getMessage());
    }
}

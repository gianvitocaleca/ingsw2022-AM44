package it.polimi.ingsw.eventsTests;

import it.polimi.ingsw.controller.events.BroadcastEvent;
import it.polimi.ingsw.network.server.networkMessages.Headers;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class tests the correct behavior of BroadcastEvent
 */
public class BroadcastEventTest {

    /**
     * This method tests the correct creation of BroadcastEvent
     */
    @Test
    public void eventCreation() {
        String message = "String";
        Headers header = Headers.action;
        BroadcastEvent evt = new BroadcastEvent(this, message, header);
        assertEquals(message, evt.getMessage());
        assertEquals(header, evt.getHeader());
    }
}

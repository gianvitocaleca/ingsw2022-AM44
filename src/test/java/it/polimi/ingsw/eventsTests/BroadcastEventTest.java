package it.polimi.ingsw.eventsTests;

import it.polimi.ingsw.controller.events.BroadcastEvent;
import it.polimi.ingsw.network.server.networkMessages.Headers;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BroadcastEventTest {

    @Test
    public void eventCreation() {
        String message = "Stringa";
        Headers header = Headers.action;
        BroadcastEvent evt = new BroadcastEvent(this, message, header);
        assertEquals(message, evt.getMessage());
        assertEquals(header, evt.getHeader());
    }
}

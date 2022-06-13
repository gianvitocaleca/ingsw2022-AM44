package it.polimi.ingsw.server.controller.Listeners;

import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.server.controller.events.ReconnectedEvent;

import java.util.EventListener;

public class ReconnectionListener implements EventListener {

    private Controller controller;

    public ReconnectionListener(Controller controller) {
        this.controller = controller;
    }

    public void eventPerformed (ReconnectedEvent evt){
        controller.reconnection(evt.getSocketID());
        controller.resumeGame();
    }
}

package it.polimi.ingsw.controller.Listeners;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.controller.events.PlanningEvent;

import java.util.EventListener;

/**
 * it is a listener of the message handler, and it manages the planning phase.
 */
public class PlanningPhaseListener implements EventListener {

    private Controller controller;

    public PlanningPhaseListener(Controller controller) {
        this.controller = controller;
    }

    /**
     * This method updates the controller with the assistant played by the current player.
     * @param evt contains the index of the assistant played.
     */
    public void eventPerformed (PlanningEvent evt){
        controller.playAssistant(evt.getIndexOfAssistant());
    }

}

package it.polimi.ingsw.controller.Listeners;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.controller.events.PlanningEvent;

import java.util.EventListener;

/**
 * it is a listener of the view proxy, and it manages the planning phase.
 */
public class PlanningPhaseListener implements EventListener {

    private Controller controller;

    public PlanningPhaseListener(Controller controller) {
        this.controller = controller;
    }

    public void eventPerformed (PlanningEvent evt){
        controller.playAssistant(evt.getIndexOfAssistant());
    }

}

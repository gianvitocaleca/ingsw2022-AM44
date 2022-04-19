package it.polimi.ingsw.controller.Listeners;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.controller.events.PlanningEvent;

import java.util.EventListener;

public class PlanningPhaseListener implements EventListener {

    private Controller controller;

    public PlanningPhaseListener(Controller controller) {
        this.controller = controller;
    }

    public void eventPerformed (PlanningEvent evt){
        controller.playAssistant(evt.getIndexOfAssistant());
    }

}

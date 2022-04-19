package it.polimi.ingsw.controller.Listeners;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.controller.events.*;

import java.util.EventListener;

public class ActionPhaseListener implements EventListener {

    private Controller controller;

    public ActionPhaseListener(Controller controller) {
        this.controller = controller;
    }

    public void eventPerformed(PlayCharacterEvent evt){
        controller.playCharacter(evt.getIndexOfCharacter());
    }

    public void eventPerformed(CharacterParametersEvent evt){
        controller.effect(evt.getParameters());
    }
}

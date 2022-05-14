package it.polimi.ingsw.server.controller.Listeners;

import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.server.controller.enums.GamePhases;
import it.polimi.ingsw.server.controller.events.CharacterParametersEvent;
import it.polimi.ingsw.server.controller.events.IntegerEvent;
import it.polimi.ingsw.server.controller.events.MoveStudentsEvent;
import it.polimi.ingsw.server.controller.events.PlayCharacterEvent;

import java.util.EventListener;

/**
 * it's a listener of the view proxy, and it manages the part of the action phase that concerns characters.
 */
public class ActionPhaseListener implements EventListener {

    private Controller controller;

    public ActionPhaseListener(Controller controller) {
        this.controller = controller;
    }

    public void eventPerformed(PlayCharacterEvent evt) {
        if (controller.getCurrentStatus().isAdvancedRules() && !controller.getCurrentPlayerPlayedCharacter()) {
            controller.playCharacter(evt.getIndexOfCharacter());
        }else{
            System.out.println("Current player tried to play the character more than one time per turn!");
        }

    }

    public void eventPerformed(CharacterParametersEvent evt) {
        if(controller.isWaitingForParameters()){
            controller.effect(evt.getParameters());
        }else{
            System.out.println("No I don't think I will!");
        }
    }

    public void eventPerformed(MoveStudentsEvent evt) {
        if (controller.getCurrentPhase().equals(GamePhases.ACTION_STUDENTSMOVEMENT)) {
            controller.moveStudents(evt);
        }else{
            System.out.println("No I don't think I will!");
        }

    }

    public void eventPerformed(IntegerEvent evt) {
        if (controller.getCurrentPhase().equals(GamePhases.ACTION_MOVEMOTHERNATURE)) {
            controller.moveMotherNature(evt.getValue());
        } else if (controller.getCurrentPhase().equals(GamePhases.ACTION_CLOUDCHOICE)) {
            controller.selectCloud(evt.getValue());
        }

    }
}

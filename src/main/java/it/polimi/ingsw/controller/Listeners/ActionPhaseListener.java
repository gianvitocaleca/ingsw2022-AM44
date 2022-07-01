package it.polimi.ingsw.controller.Listeners;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.controller.enums.GamePhases;
import it.polimi.ingsw.controller.events.CharacterParametersEvent;
import it.polimi.ingsw.controller.events.IntegerEvent;
import it.polimi.ingsw.controller.events.MoveStudentsEvent;
import it.polimi.ingsw.controller.events.PlayCharacterEvent;

import java.util.EventListener;

/**
 * it's a listener of the message handler, and it manages the part of the action phase that concerns characters.
 */
public class ActionPhaseListener implements EventListener {

    private Controller controller;

    public ActionPhaseListener(Controller controller) {
        this.controller = controller;
    }

    /**
     * This method updates the controller with the character played by the current player, if possible.
     * @param evt contains the index of the character played by the player.
     */
    public void eventPerformed(PlayCharacterEvent evt) {
        if (controller.getCurrentStatus().isAdvancedRules() && !controller.getCurrentPlayerPlayedCharacter()) {
            controller.playCharacter(evt.getIndexOfCharacter());
        }else{
            System.out.println("Current player tried to play a character more than one time per turn!");
            controller.sendErrorMessage("You can't play a character more than one time per turn!");
        }

    }

    /**
     * This method is used to play a character
     * @param evt contains player's choices to use the effect.
     */
    public void eventPerformed(CharacterParametersEvent evt) {
        if(controller.isWaitingForParameters()){
            controller.effect(evt.getParameters());
        }else{
            System.out.println("No I don't think I will!");
        }
    }

    /**
     * It manages moveStudents during the action phase.
     * @param evt contains the destination of the movement and the type of student to move
     * from the entrance.
     */
    public void eventPerformed(MoveStudentsEvent evt) {
        if (controller.getCurrentPhase().equals(GamePhases.ACTION_STUDENTS_MOVEMENT)) {
            controller.moveStudents(evt);
        }else{
            System.out.println("No I don't think I will!");
        }

    }

    /**
     * It is used to manage the two phases of the action phase that need an integer.
     * @param evt contains the number of steps mother nature has to do, or the index of the cloud.
     */
    public void eventPerformed(IntegerEvent evt) {
        if (controller.getCurrentPhase().equals(GamePhases.ACTION_MOVE_MOTHER_NATURE)) {
            controller.moveMotherNature(evt.getValue());
        } else if (controller.getCurrentPhase().equals(GamePhases.ACTION_CLOUD_CHOICE)) {
            controller.selectCloud(evt.getValue());
        }

    }
}

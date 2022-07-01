package it.polimi.ingsw.controller.Listeners;

import it.polimi.ingsw.controller.events.DisconnectionEvent;
import it.polimi.ingsw.controller.events.ReconnectedEvent;
import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.model.exceptions.PausedException;

import java.util.EventListener;

/**
 * This is a listener of the message handler, it manages user's reconnections.
 */
public class ReconnectionListener implements EventListener {

    private Controller controller;

    public ReconnectionListener(Controller controller) {
        this.controller = controller;
    }

    /**
     * This method informs the controller that a new player has joined the game.
     * He has the socketID contained in the event.
     * @param evt contains the socketID of the reconnected player.
     */
    public void eventPerformed (ReconnectedEvent evt){
        controller.reconnection(evt.getSocketID());
        controller.resumeGame();
    }

    /**
     * This event manages the disconnection of a player. It updates the current player if possible,
     * otherwise it pauses the game.
     * @param evt contains the socketID of the disconnected player.
     */
    public void eventPerformed (DisconnectionEvent evt){
        if(controller.getCurrentStatus().getCurrentPlayerUsername().equals(evt.getSocketID().getPlayerInfo().getUsername())){
            try {
                controller.updateCurrentPlayer();
                controller.sendPhaseMessage(controller.getCurrentStatus().getPhase().getHeader());
            } catch (PausedException e) {
                System.out.println("Game paused.");
            }
        }else if(!controller.isMoreThanTwoPlayers()){
            try {
                controller.pauseGame();
            } catch (PausedException e) {
                System.out.println("Game paused.");
            }
        }
    }
}

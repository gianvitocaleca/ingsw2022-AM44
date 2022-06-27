package it.polimi.ingsw.controller.Listeners;

import it.polimi.ingsw.controller.events.DisconnectionEvent;
import it.polimi.ingsw.controller.events.ReconnectedEvent;
import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.model.exceptions.PausedException;

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

package it.polimi.ingsw.server.controller.Listeners;

import it.polimi.ingsw.server.CreationHandler;
import it.polimi.ingsw.server.CreationState;
import it.polimi.ingsw.server.controller.enums.GamePhases;
import it.polimi.ingsw.server.controller.events.*;

import java.util.EventListener;

public class CreationPhaseListener implements EventListener {

    private CreationHandler creationHandler;
    private CreationState cs;

    public CreationPhaseListener(CreationHandler handler, CreationState cs)
    {
        this.creationHandler = handler;
        this.cs = cs;
    }

    public void eventPerformed(CreationEvent evt){
        int num = evt.getValue();
        if(cs.getPhase().equals(GamePhases.CREATION_NUMBER_OF_PLAYERS)){
            cs.setNumberOfPlayers(num);
        }else if(cs.getPhase().equals(GamePhases.CREATION_RULES)){
            cs.setAdvancedRules(num);
        }

    }

}

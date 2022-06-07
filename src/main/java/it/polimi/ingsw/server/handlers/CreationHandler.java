package it.polimi.ingsw.server.handlers;

import it.polimi.ingsw.server.states.CreationState;
import it.polimi.ingsw.server.states.NetworkState;
import it.polimi.ingsw.server.controller.enums.GamePhases;
import it.polimi.ingsw.server.controller.events.StringEvent;
import it.polimi.ingsw.server.networkMessages.Headers;

import javax.swing.event.EventListenerList;

public class CreationHandler extends Thread{
    private NetworkState networkState;
    private EventListenerList listeners = new EventListenerList();
    private CreationState cs;
    private int socketID;

    public CreationHandler(NetworkState networkState, MessageHandler messageHandler,CreationState cs, int socketID) {
        this.networkState = networkState;
        listeners.add(MessageHandler.class, messageHandler);
        this.cs = cs;
        this.socketID = socketID;
    }

    @Override
    public void run() {
        sendMessage(Headers.creationRequirementMessage_NumberOfPlayers, "Select the number of players [2 or 3]:");
        while(true) {
            int number = cs.getNumberOfPlayers();
            if (number != 2 && number != 3) {
                cs.reset();
                sendMessage(Headers.errorMessage, "Invalid number of players, try again [2 or 3]:");
            } else {
                networkState.setNumberOfPlayers(number);
                cs.reset();
                System.out.println("Number of players: " + number);
                cs.setPhase(GamePhases.CREATION_RULES);
                sendMessage(Headers.creationRequirementMessage_TypeOfRules, "Choose the rules type [0 standard|1 advanced]:");
                break;
            }
        }

        while(true){
            int number = cs.getAdvancedRules();
            if (number != 0 && number != 1) {
                cs.reset();
                sendMessage(Headers.errorMessage, "Invalid rules type selected, try again [0 or 1]:");
            } else {
                if (number == 1) {
                    networkState.setAdvancedRules(true);
                    cs.reset();
                }
                cs.setCreationPhaseEnded();
                networkState.setCreationPhaseEnded(true);
                break;
            }
        }
    }

    public void sendMessage(Headers header, String string) {
        StringEvent evt = new StringEvent(this, string, header, networkState.getSocketByID(socketID));
        for (MessageHandler event : listeners.getListeners(MessageHandler.class)) {
            event.eventPerformed(evt);
        }
    }
}
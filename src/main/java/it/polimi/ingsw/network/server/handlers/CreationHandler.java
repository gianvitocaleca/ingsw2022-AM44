package it.polimi.ingsw.network.server.handlers;

import it.polimi.ingsw.network.server.enums.ServerPhases;
import it.polimi.ingsw.network.server.SocketID;
import it.polimi.ingsw.network.server.states.CreationState;
import it.polimi.ingsw.network.server.states.NetworkState;
import it.polimi.ingsw.controller.enums.GamePhases;
import it.polimi.ingsw.controller.events.StringEvent;
import it.polimi.ingsw.network.server.networkMessages.Headers;

import javax.swing.event.EventListenerList;

public class CreationHandler extends Thread {
    private NetworkState networkState;
    private EventListenerList listeners = new EventListenerList();
    private CreationState cs;
    private SocketID socketID;

    /**
     * It handles the creation phase for a player
     * @param networkState is the current network state
     * @param messageHandler handles the messages
     * @param cs is the creation state
     * @param socketID is the player's socket ID
     */
    public CreationHandler(NetworkState networkState, MessageHandler messageHandler, CreationState cs, SocketID socketID) {
        this.networkState = networkState;
        listeners.add(MessageHandler.class, messageHandler);
        this.cs = cs;
        this.socketID = socketID;
    }

    /**
     * Starts to handle the player
     */
    @Override
    public void run() {
        numberOfPlayers();
        rulesType();
        networkState.setServerPhase(ServerPhases.LOGIN);
        socketID.setNeedsReplacement(true);
    }

    /**
     * Sets the number of players for the game
     */
    private void numberOfPlayers() {
        sendMessage(Headers.creationRequirementMessage_NumberOfPlayers, "Select the number of players [2 or 3]:");
        while (true) {
            int number = cs.getNumberOfPlayers();
            if (number != 2 && number != 3) {
                cs.reset();
                sendMessage(Headers.errorMessage, "Invalid number of players, try again [2 or 3]:");
            } else {
                networkState.setNumberOfPlayers(number);
                cs.reset();
                System.out.println("Number of players: " + number);
                cs.setPhase(GamePhases.CREATION_RULES);
                break;
            }
        }
    }

    /**
     * Sets the type of rules for the game
     */
    private void rulesType() {
        sendMessage(Headers.creationRequirementMessage_TypeOfRules, "Choose the rules type [0 standard|1 advanced]:");
        while (true) {
            int number = cs.getAdvancedRules();
            if (number != 0 && number != 1) {
                cs.reset();
                sendMessage(Headers.errorMessage, "Invalid rules type selected, try again [0 or 1]:");
            } else {
                if (number == 1) {
                    networkState.setAdvancedRules(true);
                    cs.reset();
                }
                break;
            }
        }
    }

    /**
     * Used to send a message
     * @param header is the message header
     * @param string is the content of the message
     */
    public void sendMessage(Headers header, String string) {
        StringEvent evt = new StringEvent(this, string, header, socketID.getSocket());
        for (MessageHandler event : listeners.getListeners(MessageHandler.class)) {
            event.eventPerformed(evt);
        }
    }
}
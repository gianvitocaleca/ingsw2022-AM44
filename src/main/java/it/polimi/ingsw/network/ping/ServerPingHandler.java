package it.polimi.ingsw.network.ping;

import it.polimi.ingsw.controller.events.DisconnectionEvent;
import it.polimi.ingsw.network.server.handlers.MessageHandler;
import it.polimi.ingsw.network.server.states.NetworkState;
import it.polimi.ingsw.network.server.SocketID;

import javax.swing.event.EventListenerList;
import java.util.Scanner;

public class ServerPingHandler extends PingHandler implements Runnable {

    private EventListenerList listeners = new EventListenerList();
    private Scanner in;

    /**
     * Handles the ping message for the server
     * @param ps is the connection status of the player
     * @param ns is the current network state
     * @param socketID is the player's socket ID
     * @param time is the time interval
     * @param maxNoAnswers is the max number of no answers the player can have
     */
    public ServerPingHandler(PingState ps, NetworkState ns, SocketID socketID, int time, int maxNoAnswers, Scanner in) {
        super(ps, ns, socketID, time, maxNoAnswers);
        this.in = in;
    }

    /**
     * Used to add the message handler
     * @param messageHandler handles the messages
     */
    public void addListener(MessageHandler messageHandler) {
        listeners.add(MessageHandler.class, messageHandler);
    }

    /**
     * Used to check if the player is connected
     * @return whether the player has left the game
     */
    public boolean checkConnectionStatus() {
        if (!ps.isReceived()) {
            noAnswers++;
            if (noAnswers == maxNoAnswers) {
                if (socketID.isConnected()) {
                    ns.disconnectPlayer(socketID.getId());
                    notifyDisconnection(socketID);
                    in.close();
                    System.out.println("Disconnected player " + socketID.getId() + " ,number of connected players: " + ns.getNumberOfConnectedSocket());
                }
                return true;
            }
        } else {
            ps.setReceived(false);
            noAnswers = 0;
        }
        return false;
    }

    /**
     * Used to send a message
     * @param socketID is the socket of the disconnected player
     */
    private void notifyDisconnection(SocketID socketID) {
        DisconnectionEvent evt = new DisconnectionEvent(this, socketID);
        for (MessageHandler event : listeners.getListeners(MessageHandler.class)) {
            event.eventPerformed(evt);
        }
    }

}

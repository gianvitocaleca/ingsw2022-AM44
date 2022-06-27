package it.polimi.ingsw.network.ping;

import it.polimi.ingsw.controller.events.DisconnectionEvent;
import it.polimi.ingsw.network.server.handlers.MessageHandler;
import it.polimi.ingsw.network.server.states.NetworkState;
import it.polimi.ingsw.network.server.SocketID;

import javax.swing.event.EventListenerList;

public class ServerPingHandler extends PingHandler implements Runnable {

    private EventListenerList listeners = new EventListenerList();

    public ServerPingHandler(PingState ps, NetworkState ns, SocketID socketID, int time, int maxNoAnswers) {
        super(ps, ns, socketID, time, maxNoAnswers);
    }

    public void addListener(MessageHandler messageHandler) {
        listeners.add(MessageHandler.class, messageHandler);
    }

    public boolean checkConnectionStatus() {
        if (!ps.isReceived()) {
            noAnswers++;
            if (noAnswers == maxNoAnswers) {
                if (socketID.isConnected()) {
                    ns.disconnectPlayer(socketID.getId());
                    notifyDisconnection(socketID);
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

    private void notifyDisconnection(SocketID socketID) {
        DisconnectionEvent evt = new DisconnectionEvent(this, socketID);
        for (MessageHandler event : listeners.getListeners(MessageHandler.class)) {
            event.eventPerformed(evt);
        }
    }

}

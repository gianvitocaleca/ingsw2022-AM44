package it.polimi.ingsw.network.ping;

import java.net.Socket;


public class ClientPingHandler extends PingHandler implements Runnable {
    /**
     * Handles the ping message for the client
     * @param ps is the connection status of the player
     * @param socket is the player's socket
     * @param time is the time interval
     * @param maxNoAnswers is the max number of no answers the player can have
     */
    public ClientPingHandler(PingState ps, Socket socket, int time, int maxNoAnswers) {
        super(ps, socket, time, maxNoAnswers);
    }

    /**
     * Used to check if the player is connected
     * @return whether the player has left the game
     */
    public boolean checkConnectionStatus() {
        if (!ps.isReceived()) {
            noAnswers++;
            if (noAnswers == maxNoAnswers) {
                return true;
            }
        } else {
            ps.setReceived(false);
            noAnswers = 0;
        }
        return false;
    }
}

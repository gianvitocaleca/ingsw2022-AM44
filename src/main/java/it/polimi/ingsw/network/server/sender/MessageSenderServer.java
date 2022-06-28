package it.polimi.ingsw.network.server.sender;

import it.polimi.ingsw.network.server.states.NetworkState;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class MessageSenderServer {

    private NetworkState networkState;

    /**
     * Used to send a message to a socket
     * @param networkState is the current status of the network
     */
    public MessageSenderServer(NetworkState networkState) {
        this.networkState = networkState;
    }

    /**
     * Used to send a message to all the players
     * @param message is the message to be sent
     */
    public void sendBroadcastMessage(String message) {
        PrintWriter out;
        for (Socket s : networkState.getActiveSockets()) {
            try {
                out = new PrintWriter(s.getOutputStream());
                out.println(message);
                out.flush();
            } catch (IOException ignore) {
            }
        }
    }

    /**
     * Used to send a message to a specific player
     * @param message is the message to be sent
     * @param s is the player's socket
     */
    public void sendMessage(String message, Socket s) {
        PrintWriter out;
        try {
            out = new PrintWriter(s.getOutputStream());
            out.println(message);
            out.flush();
        } catch (IOException e) {
            System.out.println("Impossible to send message, socket not connected");
        }
    }
}

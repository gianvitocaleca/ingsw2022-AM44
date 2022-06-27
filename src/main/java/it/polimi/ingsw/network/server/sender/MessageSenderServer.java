package it.polimi.ingsw.network.server.sender;

import it.polimi.ingsw.network.server.states.NetworkState;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class MessageSenderServer {

    private NetworkState networkState;

    public MessageSenderServer(NetworkState networkState) {
        this.networkState = networkState;
    }

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

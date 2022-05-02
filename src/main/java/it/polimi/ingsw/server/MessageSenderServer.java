package it.polimi.ingsw.server;

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

    public void sendMessage(String message, Socket s){
        PrintWriter out;
        try {
            out = new PrintWriter(s.getOutputStream());
            out.println(message);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

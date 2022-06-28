package it.polimi.ingsw.network.server;

import java.io.IOException;

public class Server {
    /**
     * Used to start the socket server process
     * @param port
     */
    public void start(int port) {
        SocketReceiverServer server = new SocketReceiverServer(port);
        try {
            server.startServer();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}

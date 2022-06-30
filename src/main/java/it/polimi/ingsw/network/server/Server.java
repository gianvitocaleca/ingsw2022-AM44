package it.polimi.ingsw.network.server;

import java.io.IOException;

public class Server {
    /**
     * Used to start the socket server process
     * @param port is the port used to wait for connections
     */
    public void start(int port) {
        SocketReceiverServer server = new SocketReceiverServer(port);
        server.startServer();
    }
}

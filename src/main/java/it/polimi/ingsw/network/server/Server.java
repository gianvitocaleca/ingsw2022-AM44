package it.polimi.ingsw.network.server;



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

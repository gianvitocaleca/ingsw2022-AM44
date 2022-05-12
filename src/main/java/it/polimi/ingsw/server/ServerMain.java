package it.polimi.ingsw.server;

import java.io.IOException;

public class ServerMain {
    public static void main(String[] args) {
        SocketReceiverServer server = new SocketReceiverServer(1337);
        try {
            server.startServer();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public void start(int port) {
        SocketReceiverServer server = new SocketReceiverServer(port);
        try {
            server.startServer();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}

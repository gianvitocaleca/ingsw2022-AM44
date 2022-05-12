package it.polimi.ingsw.client;

import java.io.IOException;

public class ClientMain {

    public static void main(String[] args) {
        LineClient client = new LineClient("127.0.0.1", 1337);
        try {
            client.startClient();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public void start(String address, int port) {
        LineClient client = new LineClient(address, port);
        try {
            client.startClient();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

}

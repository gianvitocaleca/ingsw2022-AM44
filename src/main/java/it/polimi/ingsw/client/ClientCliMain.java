package it.polimi.ingsw.client;

import it.polimi.ingsw.client.sender.AbstractSender;
import it.polimi.ingsw.client.sender.ConcreteCLISender;

import java.io.IOException;

public class ClientCliMain {

    /**
     * Used for development
     *
     * @param args
     */
    public static void main(String[] args) {
        AbstractSender client = new ConcreteCLISender("127.0.0.1", 1337);
        try {
            client.startClient();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Used to start the application Cli client
     *
     * @param address is the given address
     * @param port    is the given port
     */
    public static void start(String address, int port) {
        AbstractSender client = new ConcreteCLISender(address, port);
        try {
            client.startClient();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

}

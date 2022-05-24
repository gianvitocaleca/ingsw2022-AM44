package it.polimi.ingsw.client;

import javafx.application.Application;

import java.io.IOException;


public class ClientGuiMain {

    public static void start(String address, int port) {
        AbstractSender client = new ConcreteGUISender(address, port);
        Thread t = new Thread(() -> {
            try {
                client.startClient();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        t.start();
        Application.launch(ClientGui.class);
    }

    public static void main(String[] args) {
        Application.launch(ClientGui.class);
    }
}

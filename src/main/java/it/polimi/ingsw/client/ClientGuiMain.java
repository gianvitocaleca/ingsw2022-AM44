package it.polimi.ingsw.client;

import javafx.application.Application;

import java.io.IOException;


public class ClientGuiMain {

    public static void start(String address, int port) {
        ClientGui.setAddress(address);
        ClientGui.setPort(port);
        Application.launch(ClientGui.class);
    }

    public static void main(String[] args) {
        start("127.0.0.1", 1337);
    }
}

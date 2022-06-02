package it.polimi.ingsw.client;

import it.polimi.ingsw.client.GUI.ClientGui;
import javafx.application.Application;


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

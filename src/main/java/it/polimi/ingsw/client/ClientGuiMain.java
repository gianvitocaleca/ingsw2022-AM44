package it.polimi.ingsw.client;

import it.polimi.ingsw.App;
import javafx.application.Application;
import javafx.application.Platform;

import java.io.IOException;


public class ClientGuiMain {

    public static void start(String address, int port) {
        LineClient client = new LineClient(address, port);
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
        //Platform.runLater();
        Application.launch(ClientGui.class);
    }
}

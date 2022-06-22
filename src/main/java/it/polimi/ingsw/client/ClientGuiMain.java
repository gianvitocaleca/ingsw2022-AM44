package it.polimi.ingsw.client;

import it.polimi.ingsw.client.GUI.ClientGui;
import javafx.application.Application;


public class ClientGuiMain {

    /**
     * Used to start the application Gui client
     *
     * @param address
     * @param port
     */
    public static void start(String address, int port, String scale) {
        ClientGui.setAddress(address);
        ClientGui.setPort(port);
        System.setProperty("glass.win.uiScale", scale);
        Application.launch(ClientGui.class);
    }
}

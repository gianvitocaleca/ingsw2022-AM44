package it.polimi.ingsw.view;

import it.polimi.ingsw.view.GUI.ClientGui;
import it.polimi.ingsw.network.client.sender.AbstractSender;
import it.polimi.ingsw.network.client.sender.ConcreteCLISender;
import javafx.application.Application;

import java.io.IOException;


public class ViewSelector {

    /**
     * Used to start the application Gui client
     *
     * @param address is the address which the client is connecting to
     * @param port is the port used for connection
     */
    public static void startGui(String address, int port, String scale) {
        ClientGui.setAddress(address);
        ClientGui.setPort(port);
        System.setProperty("glass.win.uiScale", scale);
        Application.launch(ClientGui.class);
    }

    /**
     * Used to start the application Cli client
     *
     * @param address is the given address
     * @param port    is the given port
     */
    public static void startCli(String address, int port) {
        AbstractSender client = new ConcreteCLISender(address, port);
        try {
            client.startClient();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}

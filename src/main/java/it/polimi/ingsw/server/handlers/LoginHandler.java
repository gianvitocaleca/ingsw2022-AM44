package it.polimi.ingsw.server.handlers;

import it.polimi.ingsw.client.CliColors;
import it.polimi.ingsw.client.OS;
import it.polimi.ingsw.server.states.CreationState;
import it.polimi.ingsw.server.states.LoginState;
import it.polimi.ingsw.server.states.NetworkState;
import it.polimi.ingsw.server.SocketID;
import it.polimi.ingsw.server.controller.events.StringEvent;
import it.polimi.ingsw.server.model.enums.Color;
import it.polimi.ingsw.server.model.enums.Wizard;
import it.polimi.ingsw.server.networkMessages.*;

import javax.swing.event.EventListenerList;
import java.net.Socket;
import java.util.EventListener;

public class LoginHandler extends Thread implements EventListener {
    private NetworkState networkState;
    private SocketID socketId;

    private LoginState loginState;

    private EventListenerList listeners = new EventListenerList();
    private CreationState cs;

    private String title =
            "                                                                       \n" +
                    "                                                           ,,          \n" +
                    "`7MM\"\"\"YMM                                          mm     db          \n" +
                    "  MM    `7                                          MM                 \n" +
                    "  MM   d    `7Mb,od8 `7M'   `MF',6\"Yb.  `7MMpMMMb.mmMMmm `7MM  ,pP\"Ybd \n" +
                    "  MMmmMM      MM' \"'   VA   ,V 8)   MM    MM    MM  MM     MM  8I   `\" \n" +
                    "  MM   Y  ,   MM        VA ,V   ,pm9MM    MM    MM  MM     MM  `YMMMa. \n" +
                    "  MM     ,M   MM         VVV   8M   MM    MM    MM  MM     MM  L.   I8 \n" +
                    ".JMMmmmmMMM .JMML.       ,V    `Moo9^Yo..JMML  JMML.`Mbmo.JMML.M9mmmP' \n" +
                    "                        ,V                                             \n" +
                    "                     OOb\"                                              \n\n\n";

    public LoginHandler(NetworkState networkState, SocketID socketId, MessageHandler listener, LoginState loginState, CreationState cs) {
        this.networkState = networkState;
        this.socketId = socketId;
        this.listeners.add(MessageHandler.class, listener);
        this.loginState = loginState;
        this.cs = cs;
        if (!OS.isWindows()) {
            title = CliColors.FG_TITLE.getCode() + title + CliColors.RST.getCode();
        }
    }

    @Override
    public void run() {
        sendMessage(Headers.LOGIN, "");
        usernameProvider();
        colorProvider();
        wizardProvider();
        networkState.setLoginPhaseEnded();
    }

    private void usernameProvider(){
        String username;
        sendMessage(Headers.loginMessage_Username, title + "Provide your username :");

        while (true) {
            username = loginState.getUsername(socketId.getSocket()).toLowerCase();
            if (username.equals("") || username.contains(" ")) {
                loginState.removeUsername(socketId.getSocket());
                sendMessage(Headers.errorMessage, "Hey, funny guy :) Nice try, but provide a username, not an empty string");
            } else if (!networkState.setUsername(socketId.getId(), username)) {
                loginState.removeUsername(socketId.getSocket());
                sendMessage(Headers.errorMessage, "Player " + username + " already in game, provide a different username");
            } else {
                break;
            }
        }
    }

    private void colorProvider(){
        Color color;
        sendMessage(Headers.loginMessage_Color, "Choose a color : 1 White 2 Black 3 Gray");

        while (true) {
            color = loginState.getColor(socketId.getSocket());
            if (color.equals(Color.WRONG)) {
                loginState.removeColor(socketId.getSocket());
                sendMessage(Headers.errorMessage, "The chosen color doesn't exist, try again : " +
                        "1 White 2 Black 3 Gray");
            } else if (!networkState.setColor(socketId.getId(), color)) {
                loginState.removeColor(socketId.getSocket());
                sendMessage(Headers.errorMessage, "The color : " + color + " is already taken!" +
                        "Provide a different color : 1 White 2 Black 3 Gray");
            } else {
                break;
            }
        }
    }

    private void wizardProvider(){
        Wizard wizard;

        sendMessage(Headers.loginMessage_Wizard, "Choose a wizard : 1 Gandalf 2 Baljeet 3 Sabrina 4 Kenji ");

        while (true) {
            wizard = loginState.getWizard(socketId.getSocket());
            if (wizard.equals(Wizard.WRONG)) {
                loginState.removeWizard(socketId.getSocket());
                sendMessage(Headers.errorMessage, "The chosen wizard doesn't exist, try again : " +
                        "1 Gandalf 2 Baljeet 3 Sabrina 4 Kenji");

            } else if (!networkState.setWizard(socketId.getId(), wizard)) {
                loginState.removeWizard(socketId.getSocket());
                sendMessage(Headers.errorMessage, "The wizard : " + wizard + " is already taken!" +
                        "Provide a different wizard : 1 Gandalf 2 Baljeet 3 Sabrina 4 Kenji");
            } else {
                break;
            }
        }
    }

    public void sendMessage(Headers header, String message) {
        StringEvent evt = new StringEvent(this, message, header, socketId.getSocket());
        for (MessageHandler event : listeners.getListeners(MessageHandler.class)) {
            event.eventPerformed(evt);
        }
    }
}


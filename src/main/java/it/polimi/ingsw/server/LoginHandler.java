package it.polimi.ingsw.server;

import it.polimi.ingsw.server.controller.events.StringEvent;
import it.polimi.ingsw.server.model.enums.Color;
import it.polimi.ingsw.server.model.enums.Wizard;
import it.polimi.ingsw.server.networkMessages.*;
import it.polimi.ingsw.server.viewProxy.MessageHandler;

import javax.swing.event.EventListenerList;
import java.net.Socket;
import java.util.EventListener;

public class LoginHandler extends Thread implements EventListener {
    private NetworkState networkState;
    private SocketID socketId;

    private LoginState loginState;

    private EventListenerList listeners = new EventListenerList();
    private CreationState cs;

    public LoginHandler(NetworkState networkState, SocketID socketId, MessageHandler listener, LoginState loginState, CreationState cs) {
        this.networkState = networkState;
        this.socketId = socketId;
        this.listeners.add(MessageHandler.class, listener);
        this.loginState = loginState;
        this.cs = cs;
    }

    public boolean isMySocket(Socket socket) {
        return this.socketId.getSocket().equals(socket);
    }

    @Override
    public void run() {
        //waits for the creation of the game
        cs.getCreationPhaseEnded();

        String username;
        Color color;
        Wizard wizard;

        System.out.println("New client connected, starting to ask information");

        sendMessage(Headers.LOGIN, "");
        sendMessage(Headers.loginMessage_Username, "Provide your username :");

        while (true) {
            username = loginState.getUsername(socketId.getSocket()).toLowerCase();
            if (username.equals("") || username.contains(" ")) {
                loginState.removeUsername(socketId.getSocket());
                sendMessage(Headers.errorMessage, "Hey, funny guy :) Nice try, but provide a username, not an empty string");
            } else if (!networkState.setUsername(socketId.id, username)) {
                loginState.removeUsername(socketId.getSocket());
                sendMessage(Headers.errorMessage, "Player " + username + " already in game, provide a different username");
            } else {
                break;
            }
        }

        sendMessage(Headers.loginMessage_Color, "Choose a color : 1 White 2 Black 3 Gray");

        while (true) {
            color = loginState.getColor(socketId.getSocket());
            if (color.equals(Color.WRONG)) {
                loginState.removeColor(socketId.getSocket());
                sendMessage(Headers.errorMessage, "The chosen color doesn't exist, try again : " +
                        "1 White 2 Black 3 Gray");
            } else if (!networkState.setColor(socketId.id, color)) {
                loginState.removeColor(socketId.getSocket());
                sendMessage(Headers.errorMessage, "The color : " + color + " is already taken!" +
                        "Provide a different color : 1 White 2 Black 3 Gray");
            } else {
                break;
            }
        }

        sendMessage(Headers.loginMessage_Wizard, "Choose a wizard : 1 Gandalf 2 Baljeet 3 Sabrina 4 Kenji ");

        while (true) {
            wizard = loginState.getWizard(socketId.getSocket());
            if (wizard.equals(Wizard.WRONG)) {
                loginState.removeWizard(socketId.getSocket());
                sendMessage(Headers.errorMessage, "The chosen wizard doesn't exist, try again : " +
                        "1 Gandalf 2 Baljeet 3 Sabrina 4 Kenji");

            } else if (!networkState.setWizard(socketId.id, wizard)) {
                loginState.removeWizard(socketId.getSocket());
                sendMessage(Headers.errorMessage, "The wizard : " + wizard + " is already taken!" +
                        "Provide a different wizard : 1 Gandalf 2 Baljeet 3 Sabrina 4 Kenji");
            } else {
                break;
            }
        }

        networkState.setLoginPhaseEnded();

    }

    public void sendMessage(Headers header, String message) {
        StringEvent evt = new StringEvent(this, message, header, socketId.getSocket());
        for (MessageHandler event : listeners.getListeners(MessageHandler.class)) {
            event.eventPerformed(evt);
        }
    }


}


package it.polimi.ingsw.network.server.handlers;

import it.polimi.ingsw.network.server.networkMessages.Headers;
import it.polimi.ingsw.view.CLI.CliColors;
import it.polimi.ingsw.view.CLI.OS;
import it.polimi.ingsw.network.server.states.LoginState;
import it.polimi.ingsw.network.server.states.NetworkState;
import it.polimi.ingsw.network.server.SocketID;
import it.polimi.ingsw.controller.events.StringEvent;
import it.polimi.ingsw.model.enums.Color;
import it.polimi.ingsw.model.enums.Wizard;

import javax.swing.event.EventListenerList;
import java.util.EventListener;

import static it.polimi.ingsw.utils.Commands.*;
import static it.polimi.ingsw.utils.TextAssets.*;

public class LoginHandler extends Thread implements EventListener {
    private NetworkState networkState;
    private SocketID socketId;

    private LoginState loginState;

    private EventListenerList listeners = new EventListenerList();

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
    /**
     * It handles the login phase for a player
     * @param networkState is the current network state
     * @param socketId is the player's socket ID
     * @param listener is the message handler
     * @param loginState is the phase state
     */
    public LoginHandler(NetworkState networkState, SocketID socketId, MessageHandler listener, LoginState loginState) {
        this.networkState = networkState;
        this.socketId = socketId;
        this.listeners.add(MessageHandler.class, listener);
        this.loginState = loginState;
        if (!OS.isWindows()) {
            title = CliColors.FG_TITLE.getCode() + title + CliColors.RST.getCode();
        }
    }

    /**
     * Starts to handle the player
     */
    @Override
    public void run() {
        sendMessage(Headers.LOGIN, "");
        usernameProvider();
        colorProvider();
        wizardProvider();
        networkState.setLoginPhaseEnded();
    }

    /**
     * Sets the player's name
     */
    private void usernameProvider() {
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

    /**
     * Sets the player's color
     */
    private void colorProvider() {
        Color color;
        String colors = whiteColorCode + " " + whiteColorText + " " + blackColorCode + " " + blackColorText + " " + greyColorCode + " " + greyColorText;
        sendMessage(Headers.loginMessage_Color, "Choose a color : " + colors);

        while (true) {
            color = loginState.getColor(socketId.getSocket());
            if (color.equals(Color.WRONG)) {
                loginState.removeColor(socketId.getSocket());
                sendMessage(Headers.errorMessage, "The chosen color doesn't exist, try again : " + colors);
            } else if (!networkState.setColor(socketId.getId(), color)) {
                loginState.removeColor(socketId.getSocket());
                sendMessage(Headers.errorMessage, "The color : " + color + " is already taken!" +
                        "Provide a different color : " + colors);
            } else {
                break;
            }
        }
    }

    /**
     * Sets the player's wizard
     */
    private void wizardProvider() {
        Wizard wizard;
        String wizards = firstWizardCode + " " + firstWizardText + " " + secondWizardCode + " " + secondWizardText +
                " " + thirdWizardCode + " " + thirdWizardText + " " + fourthWizardCode + " " + fourthWizardText;
        sendMessage(Headers.loginMessage_Wizard, "Choose a wizard : " + wizards);

        while (true) {
            wizard = loginState.getWizard(socketId.getSocket());
            if (wizard.equals(Wizard.WRONG)) {
                loginState.removeWizard(socketId.getSocket());
                sendMessage(Headers.errorMessage, "The chosen wizard doesn't exist, try again : " + wizards);

            } else if (!networkState.setWizard(socketId.getId(), wizard)) {
                loginState.removeWizard(socketId.getSocket());
                sendMessage(Headers.errorMessage, "The wizard : " + wizard + " is already taken!" +
                        "Provide a different wizard : " + wizards);
            } else {
                break;
            }
        }
    }

    /**
     * Used to send a message
     * @param header is the message header
     * @param message is the content of the message
     */
    public void sendMessage(Headers header, String message) {
        StringEvent evt = new StringEvent(this, message, header, socketId.getSocket());
        for (MessageHandler event : listeners.getListeners(MessageHandler.class)) {
            event.eventPerformed(evt);
        }
    }
}


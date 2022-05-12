package it.polimi.ingsw.server.viewProxy;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.polimi.ingsw.server.*;
import it.polimi.ingsw.server.controller.Listeners.ActionPhaseListener;
import it.polimi.ingsw.server.controller.Listeners.PlanningPhaseListener;
import it.polimi.ingsw.server.controller.enums.GamePhases;
import it.polimi.ingsw.server.controller.events.*;
import it.polimi.ingsw.server.model.enums.Color;
import it.polimi.ingsw.server.model.enums.Wizard;
import it.polimi.ingsw.server.networkMessages.*;

import javax.swing.event.EventListenerList;
import java.net.Socket;
import java.util.*;

public class MessageHandler implements EventListener {
    private Gson gson;
    private EventListenerList listeners = new EventListenerList();
    private String message;

    private MessageSenderServer mss;

    private final int ERROR = 18;

    private LoginState loginState;
    private CreationState creationState;


    public MessageHandler() {
        gson = new Gson();
    }

    public void setLoginState(LoginState loginState) {
        this.loginState = loginState;
    }

    public void setNetworkState(NetworkState networkState) {
        mss = new MessageSenderServer(networkState);
    }


    public void setCreationState(CreationState creationState) {
        this.creationState = creationState;
    }

    public void addListener(PlanningPhaseListener listener) {
        listeners.add(PlanningPhaseListener.class, listener);
    }

    public void addListener(ActionPhaseListener listener) {
        listeners.add(ActionPhaseListener.class, listener);
    }

    public void eventPerformed(StatusEvent evt, Payload payload) {
        message = gson.toJson(new Message(evt.getHeader(), payload));
        mss.sendBroadcastMessage(message);
    }

    public void eventPerformed(CharacterPlayedEvent evt) {
        message = gson.toJson(new Message(Headers.characterPlayed, new CharacterPlayedPayload(evt.getCharactersName())));
        mss.sendBroadcastMessage(message);
    }

    public void eventPerformed(ShowModelEvent evt) {
        message = gson.toJson(new Message(Headers.showModelMessage, evt.getPayload()));
        mss.sendBroadcastMessage(message);
    }

    public void eventPerformed(StringEvent evt) {
        message = gson.toJson(new Message(evt.getHeader(), new StringPayload(evt.getMessage())));
        mss.sendMessage(message, evt.getSocket());
    }

    public void eventPerformed(BroadcastEvent evt) {
        message = gson.toJson(new Message(evt.getHeader(), new StringPayload(evt.getMessage())));
        mss.sendBroadcastMessage(message);
    }

    public synchronized void eventPerformed(MessageReceivedEvent evt, Socket sourceSocket) {
        JsonObject jsonTree = JsonParser.parseString(evt.getMessage()).getAsJsonObject();
        JsonElement jsonHeader = jsonTree.get("header");
        Headers header = gson.fromJson(jsonHeader, Headers.class);

        JsonElement jsonPayload = jsonTree.get("payload");


        switch (header) {

            case creationRequirementMessage:
                StringPayload creationPayload = gson.fromJson(jsonPayload, StringPayload.class);
                int ans;
                try {
                    ans = Integer.parseInt(creationPayload.getString());
                } catch (NumberFormatException ignore) {
                    ans = ERROR;
                }
                creationMessageReceiver(ans);
                break;
            case loginMessage_Username:
                LoginPayload loginPayload = gson.fromJson(jsonPayload, LoginPayload.class);
                loginMessageReceiver(sourceSocket, loginPayload, GamePhases.LOGIN_USERNAME);
                break;
            case loginMessage_Color:
                LoginPayload loginPayload1 = gson.fromJson(jsonPayload, LoginPayload.class);
                loginMessageReceiver(sourceSocket, loginPayload1, GamePhases.LOGIN_COLOR);
                break;
            case loginMessage_Wizard:
                LoginPayload loginPayload2 = gson.fromJson(jsonPayload, LoginPayload.class);
                loginMessageReceiver(sourceSocket, loginPayload2, GamePhases.LOGIN_WIZARD);
                break;
            case winnerPlayer:
                break;
            case currentPlayer:
                break;
            case assistantToPlay:
                break;
            case LOGIN:
                break;
            case planning:
                PlanningAnswerPayload planningAnswerPayload = gson.fromJson(jsonPayload, PlanningAnswerPayload.class);
                playAssistantReceiver(new PlanningEvent(this, planningAnswerPayload.getIndexOfAssistant()));
                break;
            case ACTION_STUDENTSMOVEMENT:
                break;
            case ACTION_MOVEMOTHERNATURE:
                break;
            case ACTION_CLOUDCHOICE:
                break;
            case errorMessage:
                break;
            case action:
                break;
            case characterPlayed:
                break;
            case showModelMessage:
                break;
            default:
                break;
        }


    }


    public void creationMessageReceiver(int num) {
        if (creationState.getPhase().equals(GamePhases.CREATION_NUMBER_OF_PLAYERS)) {
            creationState.setNumberOfPlayers(num);
        } else if (creationState.getPhase().equals(GamePhases.CREATION_RULES)) {
            creationState.setAdvancedRules(num);
        }
    }

    public void loginMessageReceiver(Socket socket, LoginPayload loginPayload, GamePhases gamePhases) {
        switch (gamePhases) {
            case LOGIN_USERNAME:
                loginState.setUsername(socket, loginPayload.getString());
                break;
            case LOGIN_COLOR:
                int color;
                try {
                    color = Integer.parseInt(loginPayload.getString());
                } catch (NumberFormatException e) {
                    color = 5;
                }
                switch (color) {
                    case 1:
                        loginState.setColor(socket, Color.WHITE);
                        break;
                    case 2:
                        loginState.setColor(socket, Color.BLACK);
                        break;
                    case 3:
                        loginState.setColor(socket, Color.GREY);
                        break;
                    default:
                        loginState.setColor(socket, Color.WRONG);
                        break;
                }

                break;
            case LOGIN_WIZARD:
                int wizard;
                try {
                    wizard = Integer.parseInt(loginPayload.getString());
                } catch (NumberFormatException e) {
                    wizard = 5;
                }
                switch (wizard) {
                    case 1:
                        loginState.setWizard(socket, Wizard.GANDALF);
                        break;
                    case 2:
                        loginState.setWizard(socket, Wizard.BALJEET);
                        break;
                    case 3:
                        loginState.setWizard(socket, Wizard.SABRINA);
                        break;
                    case 4:
                        loginState.setWizard(socket, Wizard.KENJI);
                        break;
                    default:
                        loginState.setWizard(socket, Wizard.WRONG);
                        break;
                }

                break;
        }
    }

    public void playAssistantReceiver(PlanningEvent evt) {
        for (PlanningPhaseListener event : listeners.getListeners(PlanningPhaseListener.class)) {
            event.eventPerformed(evt);
        }
    }

    public void playCharacterReceiver(PlayCharacterEvent evt) {
        for (ActionPhaseListener event : listeners.getListeners(ActionPhaseListener.class)) {
            event.eventPerformed(evt);
        }
    }

    public void characterParametersReceiver(CharacterParametersEvent evt) {
        for (ActionPhaseListener event : listeners.getListeners(ActionPhaseListener.class)) {
            event.eventPerformed(evt);
        }
    }

    public void moveStudentsReceiver(MoveStudentsEvent evt) {
        for (ActionPhaseListener event : listeners.getListeners(ActionPhaseListener.class)) {
            event.eventPerformed(evt);
        }
    }

    public void integerEventReceiver(IntegerEvent evt) {
        for (ActionPhaseListener event : listeners.getListeners(ActionPhaseListener.class)) {
            event.eventPerformed(evt);
        }
    }

}

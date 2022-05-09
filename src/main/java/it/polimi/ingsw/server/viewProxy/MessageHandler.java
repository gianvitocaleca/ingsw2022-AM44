package it.polimi.ingsw.server.viewProxy;

import com.google.gson.*;
import it.polimi.ingsw.server.*;
import it.polimi.ingsw.server.controller.Listeners.ActionPhaseListener;
import it.polimi.ingsw.server.controller.Listeners.LoginPhaseListener;
import it.polimi.ingsw.server.controller.Listeners.PlanningPhaseListener;
import it.polimi.ingsw.server.controller.GameStatus;
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

    public void addListener(LoginPhaseListener listener) {
        listeners.add(LoginPhaseListener.class, listener);
    }


    public void addLoginHandler(LoginHandler loginHandler) {
        for (LoginPhaseListener event : listeners.getListeners(LoginPhaseListener.class)) {
            event.addLoginHandler(loginHandler);
        }
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
        message = gson.toJson(new Message(Headers.characterPlayed, evt.getPayload()));
        mss.sendBroadcastMessage(message);
    }

    public void eventPerformed(StringEvent evt) {
        message = gson.toJson(new Message(evt.getHeader(), new StringPayload(evt.getMessage())));
        mss.sendMessage(message, evt.getSocket());
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
                loginMessageReceiver(new LoginEvent(this, loginPayload, GamePhases.LOGIN_USERNAME, sourceSocket));
                break;
            case loginMessage_Color:
                LoginPayload loginPayload1 = gson.fromJson(jsonPayload, LoginPayload.class);
                loginMessageReceiver(new LoginEvent(this, loginPayload1, GamePhases.LOGIN_COLOR, sourceSocket));
                break;
            case loginMessage_Wizard:
                LoginPayload loginPayload2 = gson.fromJson(jsonPayload, LoginPayload.class);
                loginMessageReceiver(new LoginEvent(this, loginPayload2, GamePhases.LOGIN_WIZARD, sourceSocket));
                break;
            case winnerPlayer:
                break;
            case currentPlayer:
                break;
            case assistantToPlay:
                break;
            case LOGIN:
                break;
            case PLANNING:
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

    public void loginMessageReceiver(LoginEvent evt) {
        switch (evt.getPhase()) {
            case LOGIN_USERNAME:
                loginState.setUsername(evt.getSender(), evt.getPayload().getString());
                break;
            case LOGIN_COLOR:
                int color;
                try {
                    color = Integer.parseInt(evt.getPayload().getString());
                } catch (NumberFormatException e) {
                    color = 5;
                }
                switch (color) {
                    case 1:
                        loginState.setColor(evt.getSender(), Color.WHITE);
                        break;
                    case 2:
                        loginState.setColor(evt.getSender(), Color.BLACK);
                        break;
                    case 3:
                        loginState.setColor(evt.getSender(), Color.GREY);
                        break;
                    default:
                        loginState.setColor(evt.getSender(), Color.WRONG);
                        break;
                }

                break;
            case LOGIN_WIZARD:
                int wizard;
                try {
                    wizard = Integer.parseInt(evt.getPayload().getString());
                } catch (NumberFormatException e) {
                    wizard = 5;
                }
                switch (wizard) {
                    case 1:
                        loginState.setWizard(evt.getSender(), Wizard.GANDALF);
                        break;
                    case 2:
                        loginState.setWizard(evt.getSender(), Wizard.BALJEET);
                        break;
                    case 3:
                        loginState.setWizard(evt.getSender(), Wizard.SABRINA);
                        break;
                    case 4:
                        loginState.setWizard(evt.getSender(), Wizard.KENJI);
                        break;
                    default:
                        loginState.setWizard(evt.getSender(), Wizard.WRONG);
                        break;
                }

                break;
        }

        for (LoginPhaseListener event : listeners.getListeners(LoginPhaseListener.class)) {
            event.eventPerformed(evt);
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

package it.polimi.ingsw.server.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.polimi.ingsw.server.controller.Listeners.ActionPhaseListener;
import it.polimi.ingsw.server.controller.Listeners.PlanningPhaseListener;
import it.polimi.ingsw.server.controller.enums.GamePhases;
import it.polimi.ingsw.server.controller.events.*;
import it.polimi.ingsw.server.model.enums.Color;
import it.polimi.ingsw.server.model.enums.Creature;
import it.polimi.ingsw.server.model.enums.Wizard;
import it.polimi.ingsw.server.networkMessages.*;
import it.polimi.ingsw.server.networkMessages.payloads.*;
import it.polimi.ingsw.server.sender.MessageSenderServer;
import it.polimi.ingsw.server.states.CreationState;
import it.polimi.ingsw.server.states.LoginState;
import it.polimi.ingsw.server.states.NetworkState;

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

    public void eventPerformed(CloseConnectionEvent evt) {
        message = gson.toJson(new Message(Headers.closeConnection, new StringPayload("")));
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

            case creationRequirementMessage_NumberOfPlayers:
            case creationRequirementMessage_TypeOfRules:
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
            case planning:
                PlanningAnswerPayload planningAnswerPayload = gson.fromJson(jsonPayload, PlanningAnswerPayload.class);
                playAssistantReceiver(new PlanningEvent(this, planningAnswerPayload.getIndexOfAssistant()));
                break;
            case action:
                ActionAnswerPayload actionAnswerPayload = gson.fromJson(jsonPayload, ActionAnswerPayload.class);
                createActionEvent(actionAnswerPayload);
                break;
            case characterPlayed:
                CharactersParametersPayload charactersParametersPayload = gson.fromJson(jsonPayload,CharactersParametersPayload.class);
                characterParametersReceiver(new CharacterParametersEvent(this,charactersParametersPayload));
            case ping:
                break;
            default:
                System.out.println("Wrong header provided! How did this happen?");
                break;
        }


    }


    private void createActionEvent(ActionAnswerPayload aap) {
        if (aap.isMoveStudents()) {
            List<Creature> studentCreature = new ArrayList<>();
            studentCreature.add(aap.getStudentCreatureToMove());
            MoveStudentsEvent event = new MoveStudentsEvent(this, !aap.isDestinationDiningRoom(), aap.getClientInt(), studentCreature);
            moveStudentsReceiver(event);
        }
        if (aap.isMoveMotherNature() || aap.isSelectCloud()) {
            IntegerEvent event = new IntegerEvent(this, aap.getClientInt());
            integerEventReceiver(event);
        }
        if (aap.isPlayCharacter()) {
            PlayCharacterEvent event = new PlayCharacterEvent(this, aap.getClientInt());
            playCharacterReceiver(event);
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
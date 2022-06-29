package it.polimi.ingsw.network.client.receiver;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.polimi.ingsw.network.server.networkMessages.Headers;
import it.polimi.ingsw.network.server.networkMessages.payloads.*;
import it.polimi.ingsw.view.ClientState;
import it.polimi.ingsw.network.ping.PingState;

import java.util.NoSuchElementException;
import java.util.Scanner;

public abstract class AbstractReceiver extends Thread {
    private Scanner socketIn;
    private Gson gson;
    private String socketLine;
    protected ClientState cs;
    private PingState ps;

    public AbstractReceiver(Scanner socketIn, ClientState cs, PingState ps) {
        this.socketIn = socketIn;
        gson = new Gson();
        this.cs = cs;
        this.ps = ps;
    }

    @Override
    public void run() {
        while (true) {
            try {
                socketLine = socketIn.nextLine();
                ps.setReceived(true);
                translateMessage(socketLine);
            } catch (NoSuchElementException ignore) {
                if (ps.isCloseConnection()) {
                    break;
                }
            }
        }
        System.out.println("No server connection");
        System.exit(0);
    }

    /**
     * Converts the string into the matching payload and calls the method.
     * The matching is header based.
     *
     * @param socketLine is the message from the server
     */
    private void translateMessage(String socketLine) {
        JsonObject jsonTree = JsonParser.parseString(socketLine).getAsJsonObject();
        JsonElement jsonHeader = jsonTree.get("header");
        Headers header = gson.fromJson(jsonHeader, Headers.class);
        setHeader(header);

        JsonElement jsonPayload = jsonTree.get("payload");
        StringPayload stringPayload;
        ActionPayload actionPayload;
        CharacterPlayedPayload charPayload;
        ReconnectionPayload reconnectionPayload;
        cs.setDisconnection(header);

        switch (header) {
            case closeConnection:
                System.out.println("Connection refused");
            case showModelMessage:
                setShowModel(gson.fromJson(jsonPayload, ShowModelPayload.class));
                break;
            case loginMessage_Username:
            case loginMessage_Color:
            case loginMessage_Wizard:
            case creationRequirementMessage_NumberOfPlayers:
            case creationRequirementMessage_TypeOfRules:
            case errorMessage:
                stringPayload = gson.fromJson(jsonPayload, StringPayload.class);
                stringMessage(header, stringPayload);
                break;
            case planning:
                printModel();
                stringPayload = gson.fromJson(jsonPayload, StringPayload.class);
                cs.getModelPayload().setCurrentPlayerUsername(stringPayload.getString());
                if (stringPayload.getString().equals(cs.getUsername())) {
                    cs.setCurrentPlayer(true);
                    //PLANNING
                    planning();
                } else {
                    //CURRENT PLAYER
                    System.out.println("The current player is " + stringPayload.getString() + " and the current phase is "
                            + header);
                    cs.setCurrentPlayer(false);
                }
                break;
            case action:
                //PRINT MODEL
                printModel();
                actionPayload = gson.fromJson(jsonPayload, ActionPayload.class);
                cs.getModelPayload().setCurrentPlayerUsername(actionPayload.getCurrentPlayer());
                if (actionPayload.getCurrentPlayer().equals(cs.getUsername())) {
                    cs.setCurrentPlayer(true);
                    cs.setMoveStudents(actionPayload.isMoveStudents());
                    cs.setMoveMotherNature(actionPayload.isMoveMotherNature());
                    cs.setSelectCloud(actionPayload.isSelectCloud());
                    cs.setSelectCharacter(actionPayload.isPlayCharacter());
                    action();
                } else {
                    System.out.println("The current player is " + actionPayload.getCurrentPlayer() + " and the current phase is "
                            + header);
                    cs.setCurrentPlayer(false);
                }
                break;
            case characterPlayed:
                charPayload = gson.fromJson(jsonPayload, CharacterPlayedPayload.class);
                if (cs.getCurrentPlayer() && charPayload.getCharactersName().needsParameters()) {
                    characterParameterSelection(charPayload);
                } else if (cs.getCurrentPlayer()) {
                    System.out.println("You have played the character: " + charPayload.getCharactersName());
                } else {
                    System.out.println(cs.getModelPayload().getCurrentPlayerUsername() + " is playing " + charPayload.getCharactersName());
                }
                break;
            case winnerPlayer:
                stringPayload = gson.fromJson(jsonPayload, StringPayload.class);
                stringMessage(Headers.winnerPlayer, stringPayload);
                break;
            case reconnection:
                reconnectionPayload = gson.fromJson(jsonPayload, ReconnectionPayload.class);
                reconnectPlayer(reconnectionPayload);
                System.out.println("Mi sono riconnesso e il mio username è : " + reconnectionPayload.getUsername());
            default:
                break;
        }
    }

    abstract void reconnectPlayer(ReconnectionPayload reconnectionPayload);

    private void setHeader(Headers header) {
        if (!header.equals(Headers.ping) && !header.equals(Headers.errorMessage) &&
                !header.equals(Headers.showModelMessage)) {
            cs.setHeaders(header);
        }
    }

    private void setShowModel(ShowModelPayload payload) {
        cs.setShowModel(payload);
        if (payload.getReconnection()) {
            printModel();
        }
    }

    abstract void stringMessage(Headers header, StringPayload payload);

    abstract void printModel();

    abstract void planning();

    abstract void action();

    abstract void characterParameterSelection(CharacterPlayedPayload cpp);

}

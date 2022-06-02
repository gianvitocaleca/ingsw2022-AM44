package it.polimi.ingsw.client.receiver;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.polimi.ingsw.client.ClientState;
import it.polimi.ingsw.pingHandler.PingState;
import it.polimi.ingsw.server.networkMessages.*;
import it.polimi.ingsw.server.networkMessages.payloads.ActionPayload;
import it.polimi.ingsw.server.networkMessages.payloads.CharacterPlayedPayload;
import it.polimi.ingsw.server.networkMessages.payloads.ShowModelPayload;
import it.polimi.ingsw.server.networkMessages.payloads.StringPayload;

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

    }

    private void translateMessage(String socketLine) {
        JsonObject jsonTree = JsonParser.parseString(socketLine).getAsJsonObject();
        JsonElement jsonHeader = jsonTree.get("header");
        Headers header = gson.fromJson(jsonHeader, Headers.class);
        setHeader(header);

        JsonElement jsonPayload = jsonTree.get("payload");
        StringPayload stringPayload;
        ActionPayload actionPayload;
        CharacterPlayedPayload charPayload;

        switch (header) {
            case closeConnection:
                System.out.println("Connection refused");
                cs.setDisconnection(header);
            case showModelMessage:
                setShowModel(gson.fromJson(jsonPayload, ShowModelPayload.class));
                break;
            case loginMessage_Username:
                cs.setDisconnection(header);
            case loginMessage_Color:
            case loginMessage_Wizard:
            case creationRequirementMessage_NumberOfPlayers:
                cs.setDisconnection(header);
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
                System.out.println("The winner is " + stringPayload.getString());
                break;
        }
    }

    private void setHeader(Headers header) {
        if (!header.equals(Headers.ping) && !header.equals(Headers.errorMessage) &&
                !header.equals(Headers.showModelMessage)) {
            cs.setHeaders(header);
        }
    }

    private void setShowModel(ShowModelPayload payload) {
        cs.setShowModel(payload);
    }

    abstract void stringMessage(Headers header, StringPayload payload);

    abstract void printModel();

    abstract void planning();

    abstract void action();

    abstract void characterParameterSelection(CharacterPlayedPayload cpp);

}

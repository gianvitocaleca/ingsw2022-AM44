package it.polimi.ingsw.client;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.polimi.ingsw.pingHandler.PingState;
import it.polimi.ingsw.server.model.enums.Name;
import it.polimi.ingsw.server.model.player.Assistant;
import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.server.networkMessages.ActionPayload;
import it.polimi.ingsw.server.networkMessages.Headers;
import it.polimi.ingsw.server.networkMessages.ShowModelPayload;
import it.polimi.ingsw.server.networkMessages.StringPayload;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class MessageReceiverClient extends Thread {
    private Scanner socketIn;
    private Gson gson;
    private String socketLine;
    private ClientState cs;
    private PingState ps;

    public MessageReceiverClient(Scanner socketIn, ClientState cs, PingState ps) {
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

        switch (header) {
            case showModelMessage:
                setShowModel(gson.fromJson(jsonPayload, ShowModelPayload.class));
                break;
            case loginMessage_Username:
            case loginMessage_Color:
            case loginMessage_Wizard:
            case creationRequirementMessage:
            case errorMessage:
                stringPayload = gson.fromJson(jsonPayload, StringPayload.class);
                System.out.println(stringPayload.getString());
                break;
            case planning:
                stringPayload = gson.fromJson(jsonPayload, StringPayload.class);
                if (stringPayload.getString().equals(cs.getUsername())) {
                    cs.setCurrentPlayer(true);
                    planning();
                } else {
                    System.out.println("The current player is " + stringPayload.getString() + " and the current phase is "
                            + header);
                    cs.setCurrentPlayer(false);
                }
                break;
            case action:
                actionPayload = gson.fromJson(jsonPayload, ActionPayload.class);
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
        printModel();
    }

    private void printModel() {
        System.out.print(cs.getModelCache());
    }

    private void planning() {
        System.out.println("Which assistant do you want to play? ");
        List<Player> playerList = cs.getModelCache().getPlayersList();
        Player me = playerList.stream().filter(p -> p.getUsername().equals(cs.getUsername())).toList().get(0);

        for (int i = 0; i < me.getAssistantDeck().size(); i++) {
            Assistant assistant = me.getAssistantDeck().get(i);
            System.out.println(i + ": " + assistant.getName() + " value: " +
                    assistant.getValue() + " movements: " + assistant.getMovements());
        }
    }

    private void action() {
        System.out.println(":=: Allowed actions in this turn :=:");
        if (cs.isMoveStudents()) {
            System.out.println(":=: Move students <MS> :=:");
            System.out.println("Specify the student creature <R,G,Y,B,P>");
            System.out.println("Specify the destination <0," + cs.getModelCache().getIslands().size() + "> (0 is your Dinig Room, the others are the islands)");
            System.out.println("For example MS:R:2");
        }
        if (cs.isMoveMotherNature()) {
            System.out.println(":=: Move mother nature <MMN> :=:");
            System.out.println("Specify the number of jumps you want to make");
            System.out.println("For example MMN:3");
        }
        if (cs.isSelectCloud()) {
            System.out.println(":=: Select cloud <SC> :=:");
            System.out.println("Choose a cloud from which you want to take the new students to put in your entrance");
            System.out.println("<0," + (cs.getModelCache().getClouds().size() - 1) + "> clouds available");
            System.out.println("For example SC:1");
        }
        if (cs.isSelectCharacter()) {
            System.out.println(":=: Play character <PC> :=:");
            System.out.println("Choose a character to play");
            List<Name> chars = cs.getModelCache().getCharacters().keySet().stream().toList();
            int i;
            for (i = 0; i < chars.size() - 1; i++) {
                System.out.print(" " + i + ":" + chars.get(i) + ", ");
            }
            System.out.print(" " + i + ":" + chars.get(i) + " \n");
            System.out.println("For example PC:2");
        }


    }
}

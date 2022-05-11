package it.polimi.ingsw.client;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.polimi.ingsw.pingHandler.PingState;
import it.polimi.ingsw.server.model.player.Assistant;
import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.server.networkMessages.ActionPayload;
import it.polimi.ingsw.server.networkMessages.Headers;
import it.polimi.ingsw.server.networkMessages.ShowModelPayload;
import it.polimi.ingsw.server.networkMessages.StringPayload;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintWriter;
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
            case PLANNING:
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
                    action(actionPayload);
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
        System.out.println(cs.getModelCache());
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

    private void action(ActionPayload actionPayload) {

    }
}

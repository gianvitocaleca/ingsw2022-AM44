package it.polimi.ingsw.client;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.polimi.ingsw.pingHandler.PingState;
import it.polimi.ingsw.server.controller.events.CharacterPlayedEvent;
import it.polimi.ingsw.server.model.enums.Name;
import it.polimi.ingsw.server.model.player.Assistant;
import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.server.networkMessages.*;

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
        CharacterPlayedPayload charPayload;

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
                printModel();
                stringPayload = gson.fromJson(jsonPayload, StringPayload.class);
                cs.getModelCache().setCurrentPlayerUsername(stringPayload.getString());
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
                printModel();
                actionPayload = gson.fromJson(jsonPayload, ActionPayload.class);
                cs.getModelCache().setCurrentPlayerUsername(actionPayload.getCurrentPlayer());
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
                } else if(cs.getCurrentPlayer()){
                    System.out.println("You have played the character: " + charPayload.getCharactersName());
                }else{
                    System.out.println(cs.getModelCache().getCurrentPlayerUsername() + " is playing " + charPayload.getCharactersName());
                }
                break;
            case winnerPlayer:
                stringPayload = gson.fromJson(jsonPayload,StringPayload.class);
                System.out.println("The winner is "+stringPayload.getString());
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
            cs.getModelCache().getCharacters().stream().forEach(c -> System.out.println(c.getIndex()+":"+c.getName()+":"+c.getCost()));
            System.out.println("\nFor example PC:2");
        }


    }

    private void characterParameterSelection(CharacterPlayedPayload cpp) {
        Name character = cpp.getCharactersName();
        cs.setCurrentPlayedCharacter(character);
        if (character.isNeedsSourceCreature() && character.isNeedsDestination()) {
            System.out.println("Choose the creature from the character card and the destination Island on which to put it");
            System.out.println("C:G:I:7");
        } else if (character.isNeedsSourceCreature() && character.isNeedsDestinationCreature()) {
            System.out.println("Which creatures from the character card and the destination do you want to swap?");
            System.out.println("For example to swap character (C) creatures Red, Green and Blue  with " +
                    "the destination (D) creatures Blue, Yellow and Pink use the following syntax");
            System.out.println("C:R,G,B:D:B,Y,P");
            System.out.println("Select at most "+character.getMaxMoves()+" creatures");
        } else if (character.isNeedsIslandIndex()) {
            System.out.println("Which island do you want to choose?");
        } else if (character.isNeedsSourceCreature()) {
            System.out.println("Which Creature do you want to choose?");
        } else if (character.isNeedsMnMovements()) {
            System.out.println("How many more jumps do you want Mother Nature to do?");
        }
    }

}

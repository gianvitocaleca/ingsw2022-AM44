package it.polimi.ingsw.client;

import it.polimi.ingsw.pingHandler.PingState;
import it.polimi.ingsw.server.model.enums.Name;
import it.polimi.ingsw.server.model.player.Assistant;
import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.server.networkMessages.CharacterPlayedPayload;
import it.polimi.ingsw.server.networkMessages.Headers;
import it.polimi.ingsw.server.networkMessages.StringPayload;

import java.util.List;
import java.util.Scanner;

public class ConcreteCLIReceiver extends AbstractReceiver {

    public ConcreteCLIReceiver(Scanner socketIn, ClientState cs, PingState ps) {
        super(socketIn, cs, ps);
    }

    @Override
    void stringMessage(Headers header, StringPayload payload) {
        System.out.println(payload.getString());
    }

    void printModel() {
        System.out.print(cs.getModelCache());
    }

    @Override
    void planning() {
        System.out.println("Which assistant do you want to play? ");
        List<Player> playerList = cs.getModelCache().getPlayersList();
        Player me = playerList.stream().filter(p -> p.getUsername().equals(cs.getUsername())).toList().get(0);

        for (int i = 0; i < me.getAssistantDeck().size(); i++) {
            Assistant assistant = me.getAssistantDeck().get(i);
            System.out.println(i + ": " + assistant.getName() + " value: " +
                    assistant.getValue() + " movements: " + assistant.getMovements());
        }
    }

    @Override
    void action() {
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

    @Override
    void characterParameterSelection(CharacterPlayedPayload cpp) {
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

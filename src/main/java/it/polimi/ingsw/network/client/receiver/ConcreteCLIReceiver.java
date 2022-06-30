package it.polimi.ingsw.network.client.receiver;

import it.polimi.ingsw.view.CLI.CliColors;
import it.polimi.ingsw.view.CLI.CliPrinter;
import it.polimi.ingsw.view.ClientState;
import it.polimi.ingsw.view.CLI.OS;
import it.polimi.ingsw.network.ping.PingState;
import it.polimi.ingsw.model.enums.Name;
import it.polimi.ingsw.model.player.Assistant;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.network.server.networkMessages.payloads.CharacterPlayedPayload;
import it.polimi.ingsw.network.server.networkMessages.Headers;
import it.polimi.ingsw.network.server.networkMessages.payloads.ReconnectionPayload;
import it.polimi.ingsw.network.server.networkMessages.payloads.ShowModelPayload;
import it.polimi.ingsw.network.server.networkMessages.payloads.StringPayload;

import static it.polimi.ingsw.utils.Commands.*;
import static it.polimi.ingsw.utils.TextAssets.cliMenu;
import static it.polimi.ingsw.utils.TextAssets.exampleText;

import java.util.List;
import java.util.Scanner;

public class ConcreteCLIReceiver extends AbstractReceiver {

    private ShowModelPayload modelPayload;
    private final CliPrinter printer = new CliPrinter();

    /**
     * Waits for server messages.
     * Decodes and shows them to the player.
     * @param socketIn is the player's socket
     * @param cs is the current player's state
     * @param ps is the current connection state
     */
    public ConcreteCLIReceiver(Scanner socketIn, ClientState cs, PingState ps) {
        super(socketIn, cs, ps);
    }

    /**
     * Prints the payload content,
     *
     * @param header  the given payload header
     * @param payload the given payload
     */
    @Override
    void stringMessage(Headers header, StringPayload payload) {
        if (header.equals(Headers.winnerPlayer)) {
            System.out.print(payload.getString() + "has won the game!");
        }
        System.out.println(payload.getString());
    }

    /**
     * Prints the payload to the standard output.
     */
    @Override
    void printModel() {
        modelPayload = cs.getModelPayload();
        if (!OS.isWindows()) {
            System.out.println(CliColors.ERASE_SCREEN.getCode());
        }
        printPlayers();
        printIslands();
        printClouds();
        if (modelPayload.isAdvancedRules()) {
            printTable();
        }
    }

    /**
     * Prints the information for the player during the planning phase
     */
    @Override
    void planning() {
        System.out.println("Which assistant do you want to play? ");
        List<Player> playerList = cs.getModelPayload().getPlayersList();
        Player me = playerList.stream().filter(p -> p.getUsername().equals(cs.getUsername())).toList().get(0);

        for (int i = 0; i < me.getAssistantDeck().size(); i++) {
            Assistant assistant = me.getAssistantDeck().get(i);
            System.out.println(i + ": " + assistant.getName() + " value: " +
                    assistant.getValue() + " movements: " + assistant.getMovements());
        }
    }

    /**
     * Prints the information for the player during the action phase
     */
    @Override
    void action() {
        System.out.println(cliMenu+" Allowed actions in this turn "+cliMenu);
        if (cs.isMoveStudents()) {
            System.out.println(cliMenu+" Move students <" + moveStudentsCode + "> "+cliMenu);
            System.out.println("Specify the student creature <" +
                    redCreatureText + "," + greenCreatureText + "," + blueCreatureText + "," + yellowCreatureText + "," + pinkCreatureText + ">");
            System.out.println("Specify the destination <0," + cs.getModelPayload().getIslands().size() + "> (0 is your Dinig Room, the others are the islands)");
            System.out.println("For example " + moveStudentsCode + commandSeparator + redCreatureText + commandSeparator + "2");
        }
        if (cs.isMoveMotherNature()) {
            System.out.println(cliMenu+" Move mother nature <" + moveMotherNatureCode + "> "+cliMenu);
            System.out.println("Specify the number of jumps you want to make");
            System.out.println(exampleText + moveMotherNatureCode + commandSeparator + "3");
        }
        if (cs.isSelectCloud()) {
            System.out.println(cliMenu+" Select cloud <" + selectCloudCode + "> "+cliMenu);
            System.out.println("Choose a cloud from which you want to take the new students to put in your entrance");
            System.out.println("<0," + (cs.getModelPayload().getClouds().size() - 1) + "> clouds available");
            System.out.println(exampleText+ selectCloudCode + commandSeparator + "1");
        }
        if (cs.isSelectCharacter()) {
            System.out.println(cliMenu+" Play character <" + playCharacterCode + "> "+cliMenu);
            System.out.println("Choose a character to play");
            cs.getModelPayload().getCharacters().stream().forEach(c -> System.out.println(c.getIndex() + ":" + c.getName() + ":" + c.getCost()));
            System.out.println(exampleText + playCharacterCode + commandSeparator + "2");
        }
    }

    /**
     * Prints the information for the player during the character parameter selection phase
     *
     * @param cpp is the payload that contains the character parameters selection information
     */
    @Override
    void characterParameterSelection(CharacterPlayedPayload cpp) {
        Name character = cpp.getCharactersName();
        cs.setCurrentPlayedCharacter(character);
        if (character.isNeedsSourceCreature() && character.isNeedsDestination()) {
            System.out.println("Choose the creature from the character card and the destination Island on which to put it");
            System.out.println(selectCreatureText + commandSeparator + greenCreatureText + commandSeparator + selectIslandText + commandSeparator + "7");
        } else if (character.isNeedsSourceCreature() && character.isNeedsDestinationCreature()) {
            System.out.println("Which creatures from the character card and the destination do you want to swap?");
            System.out.println("For example to swap character (" + selectCreatureText + ") creatures Red, Green and Blue  with " +
                    "the destination (" + selectDestinationText + ") creatures Blue, Yellow and Pink use the following syntax");
            System.out.println(selectCreatureText + commandSeparator + redCreatureText + creatureSeparator + greenCreatureText + creatureSeparator + blueCreatureText +
                    commandSeparator + selectDestinationText + commandSeparator + blueCreatureText + creatureSeparator + yellowCreatureText + creatureSeparator + pinkCreatureText);
            System.out.println("Select at most " + character.getMaxMoves() + " creatures");
        } else if (character.isNeedsIslandIndex()) {
            System.out.println("Which island do you want to choose?");
        } else if (character.isNeedsSourceCreature()) {
            System.out.println("Which Creature do you want to choose?");
        } else if (character.isNeedsMnMovements()) {
            System.out.println("How many more jumps do you want Mother Nature to do?");
        }
    }

    /**
     * This method sets the username of the client after a reconnection.
     * The client knows his username in this way.
     *
     * @param reconnectionPayload is the object that contains information about client's username.
     */
    void reconnectPlayer(ReconnectionPayload reconnectionPayload) {
        cs.setUsername(reconnectionPayload.getUsername());
        System.out.println("Reconnected, my username is: " + reconnectionPayload.getUsername());
    }

    /**
     * Prints the player's information on the standard output
     */
    private void printPlayers() {
        printer.printPlayers(cs.getModelPayload());
    }


    /**
     * Prints the island's information on the standard output
     */
    private void printIslands() {
        printer.printIslands(cs.getModelPayload());
    }

    /**
     * Prints the cloud's information on the standard output
     */
    private void printClouds() {
        printer.printClouds(cs.getModelPayload());
    }

    /**
     * Prints the table's information on the standard output
     */
    private void printTable() {
        printer.printTable(cs.getModelPayload());
    }
}

package it.polimi.ingsw.network.client.receiver;

import it.polimi.ingsw.view.GUI.ClientGui;
import it.polimi.ingsw.view.ClientState;
import it.polimi.ingsw.view.GUI.GUIPhases;
import it.polimi.ingsw.network.ping.PingState;
import it.polimi.ingsw.model.enums.Name;
import it.polimi.ingsw.network.server.networkMessages.payloads.CharacterPlayedPayload;
import it.polimi.ingsw.network.server.networkMessages.Headers;
import it.polimi.ingsw.network.server.networkMessages.payloads.ReconnectionPayload;
import it.polimi.ingsw.network.server.networkMessages.payloads.StringPayload;
import javafx.application.Platform;

import java.util.Scanner;

import static it.polimi.ingsw.utils.Commands.commandSeparator;
import static it.polimi.ingsw.utils.Commands.selectCreatureText;
import static it.polimi.ingsw.view.GUI.GuiAlerts.*;

public class ConcreteGUIReceiver extends AbstractReceiver {

    private static ClientGui clientGui;

    /**
     * Waits for server messages.
     * Decodes and shows them to the player.
     * @param socketIn the player's socket
     * @param cs is the current player's state
     * @param ps is the current connection state
     * @param clientGui is the graphical interface class
     */
    public ConcreteGUIReceiver(Scanner socketIn, ClientState cs, PingState ps, ClientGui clientGui) {
        super(socketIn, cs, ps);
        this.clientGui = clientGui;
    }

    /**
     * Calls the runLater method to update JFX graphics
     *
     * @param header  is the given payload header
     * @param payload is the given payload, which contains the updated information
     */
    @Override
    void stringMessage(Headers header, StringPayload payload) {
        switch (header) {
            case creationRequirementMessage_NumberOfPlayers:
                Platform.runLater(() -> clientGui.numberOfPlayers());
                break;
            case creationRequirementMessage_TypeOfRules:
                Platform.runLater(() -> clientGui.typeOfRules());
                break;
            case loginMessage_Username:
                Platform.runLater(() -> clientGui.loginUsername());
                break;
            case loginMessage_Color:
                Platform.runLater(() -> clientGui.color());
                break;
            case loginMessage_Wizard:
                Platform.runLater(() -> clientGui.wizard());
                break;
            case errorMessage:
                Platform.runLater(() -> errorAlert(payload.getString()));
                Platform.runLater(() -> clientGui.setMoveStudents());
                break;
            case winnerPlayer:
                Platform.runLater(() -> winnerAlert(payload.getString()));
                break;
            default:
                break;
        }
    }

    /**
     * Updates the game pane
     */
    @Override
    void printModel() {
        Platform.runLater(() -> clientGui.updateClientState(cs));
        Platform.runLater(() -> clientGui.gamePaneGenerator(cs.getModelPayload()));
    }

    /**
     * Updates the game pane, during planning phase
     */
    @Override
    void planning() {
        Platform.runLater(() -> clientGui.gamePaneGenerator(cs.getModelPayload()));
    }

    /**
     * Updates the game pane, during action phase
     */
    @Override
    void action() {
        Platform.runLater(() -> clientGui.setMoveStudents());
        Platform.runLater(() -> clientGui.gamePaneGenerator(cs.getModelPayload()));
    }

    /**
     * Calls different methods based on the type of character played
     *
     * @param cpp is the payload with the character played
     */
    @Override
    void characterParameterSelection(CharacterPlayedPayload cpp) {
        Name character = cpp.getCharactersName();
        cs.setCurrentPlayedCharacter(character);
        if (character.isNeedsSourceCreature() && character.isNeedsDestination()) {
            clientGui.setGuiPhases(GUIPhases.SELECT_CREATURE_FOR_CHARACTER);
            Platform.runLater(() -> characterNeedsSourceCreaturesAndDestination(cs));
        } else if (character.isNeedsSourceCreature() && character.isNeedsDestinationCreature()) {
            Platform.runLater(() -> {
                clientGui.setGuiPhases(GUIPhases.SELECT_SOURCE_CREATURE_TO_SWAP);
                clientGui.addCreatedCommand(selectCreatureText + commandSeparator);
                clientGui.createSwapButton(true);
                characterNeedsSwapCreatures(cs);
            });
        } else if (character.isNeedsIslandIndex()) {
            clientGui.setGuiPhases(GUIPhases.SELECT_ISLAND);
            Platform.runLater(() -> characterNeedsIslandIndex(cs));
        } else if (character.isNeedsMnMovements()) {
            Platform.runLater(() -> clientGui.characterNeedsMMNMovements());
        } else if (character.isNeedsSourceCreature()) {
            clientGui.setGuiPhases(GUIPhases.SELECT_SOURCE_CREATURE);
            Platform.runLater(() -> characterNeedsSourceCreature(cs));
        }
    }

    /**
     * Used to update the player username when reconnection happens
     *
     * @param reconnectionPayload is the payload with the username information
     */
    void reconnectPlayer(ReconnectionPayload reconnectionPayload) {
        cs.setUsername(reconnectionPayload.getUsername());
        clientGui.setMY_USERNAME(reconnectionPayload.getUsername());
    }
}

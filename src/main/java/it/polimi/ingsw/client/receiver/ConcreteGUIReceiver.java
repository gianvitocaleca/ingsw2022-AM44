package it.polimi.ingsw.client.receiver;

import it.polimi.ingsw.client.GUI.ClientGui;
import it.polimi.ingsw.client.ClientState;
import it.polimi.ingsw.pingHandler.PingState;
import it.polimi.ingsw.server.model.enums.Name;
import it.polimi.ingsw.server.networkMessages.payloads.CharacterPlayedPayload;
import it.polimi.ingsw.server.networkMessages.Headers;
import it.polimi.ingsw.server.networkMessages.payloads.ReconnectionPayload;
import it.polimi.ingsw.server.networkMessages.payloads.StringPayload;
import javafx.application.Platform;

import java.util.Scanner;

public class ConcreteGUIReceiver extends AbstractReceiver {

    private static ClientGui clientGui;

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
                Platform.runLater(() -> clientGui.errorAlert(payload.getString()));
                Platform.runLater(() -> clientGui.setMoveStudents());
                break;
            case winnerPlayer:
                Platform.runLater(() -> clientGui.winnerAlert(payload.getString()));
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
            Platform.runLater(() -> clientGui.characterNeedsSourceCreaturesAndDestination());
        } else if (character.isNeedsSourceCreature() && character.isNeedsDestinationCreature()) {
            Platform.runLater(() -> clientGui.characterNeedsSwapCreatures());
        } else if (character.isNeedsIslandIndex()) {
            Platform.runLater(() -> clientGui.characterNeedsIslandIndex());
        } else if (character.isNeedsMnMovements()) {
            Platform.runLater(() -> clientGui.characterNeedsMMNMovements());
        } else if (character.isNeedsSourceCreature()) {
            Platform.runLater(() -> clientGui.characterNeedsSourceCreature());
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

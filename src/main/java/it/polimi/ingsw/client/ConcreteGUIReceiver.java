package it.polimi.ingsw.client;

import it.polimi.ingsw.pingHandler.PingState;
import it.polimi.ingsw.server.networkMessages.CharacterPlayedPayload;
import it.polimi.ingsw.server.networkMessages.Headers;
import it.polimi.ingsw.server.networkMessages.StringPayload;
import javafx.application.Platform;

import java.util.Scanner;

public class ConcreteGUIReceiver extends AbstractReceiver {

    private static ClientGui clientGui;

    public ConcreteGUIReceiver(Scanner socketIn, ClientState cs, PingState ps, ClientGui clientGui) {
        super(socketIn, cs, ps);
        this.clientGui = clientGui;
    }

    @Override
    void stringMessage(Headers header, StringPayload payload) {
        switch (header) {
            case creationRequirementMessage_NumberOfPlayers:
                Platform.runLater(() -> clientGui.creationMechanics());
                break;
        }
    }

    @Override
    void printModel() {

    }

    @Override
    void planning() {

    }

    @Override
    void action() {

    }

    @Override
    void characterParameterSelection(CharacterPlayedPayload cpp) {

    }
}

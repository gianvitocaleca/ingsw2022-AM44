package it.polimi.ingsw.client;

import it.polimi.ingsw.pingHandler.PingState;
import it.polimi.ingsw.server.networkMessages.CharacterPlayedPayload;
import it.polimi.ingsw.server.networkMessages.StringPayload;

import java.util.Scanner;

public class ConcreteGUIReceiver extends  AbstractReceiver{
    public ConcreteGUIReceiver(Scanner socketIn, ClientState cs, PingState ps) {
        super(socketIn, cs, ps);
    }

    @Override
    void stringMessage(StringPayload payload) {

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

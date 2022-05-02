package it.polimi.ingsw.server.controller.events;

import it.polimi.ingsw.server.ServerPhases;
import it.polimi.ingsw.server.controller.enums.GamePhases;
import it.polimi.ingsw.server.networkMessages.LoginPayload;

import java.net.Socket;
import java.util.EventObject;

public class LoginEvent extends EventObject {

    private LoginPayload payload;
    private GamePhases phase;
    private Socket sender;

    public LoginEvent(Object source, LoginPayload payload, GamePhases phase, Socket sender) {
        super(source);
        this.payload = payload;
        this.phase = phase;
        this.sender = sender;
    }

    public LoginPayload getPayload() {
        return payload;
    }

    public GamePhases getPhase() {
        return phase;
    }

    public Socket getSender() {
        return sender;
    }
}

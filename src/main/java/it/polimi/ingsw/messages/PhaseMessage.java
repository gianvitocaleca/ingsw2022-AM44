package it.polimi.ingsw.messages;

import it.polimi.ingsw.controller.enums.GamePhases;

public class PhaseMessage {

    private Headers phase;

    public PhaseMessage(Headers phase) {
        this.phase = phase;
    }

    public Headers getPhase() {
        return phase;
    }
}

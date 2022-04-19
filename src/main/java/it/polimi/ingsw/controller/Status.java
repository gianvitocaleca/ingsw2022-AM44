package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.enums.GamePhases;

public class Status {

    private GamePhases phase;
    private boolean waitingForParameters = false;

    public Status(GamePhases phase) {
        this.phase = phase;
    }

    public GamePhases getPhase() {
        return phase;
    }

    public boolean isWaitingForParameters(){
        return waitingForParameters;
    }

    public void toggleWaitingForParameters(){
        waitingForParameters=!waitingForParameters;
    }

    public void setPhase(GamePhases phase) {
        this.phase = phase;
    }
}

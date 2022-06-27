package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.enums.GamePhases;

/**
 * this class is used to keep track of game's phases.
 */
public class GameStatus {
    private GamePhases phase;
    private boolean waitingForParameters = false;
    private String currentPlayerUsername;
    private int numberOfStudentsMoved = 0;
    private boolean advancedRules;

    public GameStatus(GamePhases phase, boolean advancedRules) {
        this.phase = phase;
        this.advancedRules = advancedRules;
    }

    public void setAdvancedRules(boolean isAdvanced){
        advancedRules=isAdvanced;
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

    public String getCurrentPlayerUsername() {
        return currentPlayerUsername;
    }

    public void setCurrentPlayerUsername(String currentPlayerUsername) {
        this.currentPlayerUsername = currentPlayerUsername;
    }

    public int getNumberOfStudentsMoved() {
        return numberOfStudentsMoved;
    }

    public void setNumberOfStudentsMoved(int numberOfStudentsMoved) {
        this.numberOfStudentsMoved = numberOfStudentsMoved;
    }

    public boolean isAdvancedRules() {
        return advancedRules;
    }
}

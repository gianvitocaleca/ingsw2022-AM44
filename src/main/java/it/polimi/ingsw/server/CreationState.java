package it.polimi.ingsw.server;

import it.polimi.ingsw.server.controller.enums.GamePhases;

public class CreationState {
    private int numberOfPlayers;
    private int advancedRules;
    private GamePhases phase;
    private Boolean creationPhaseEnded = false;

    public CreationState() {
        this.numberOfPlayers = 0;
        this.advancedRules = -1;
        this.phase = GamePhases.CREATION_NUMBER_OF_PLAYERS;
    }

    public int getNumberOfPlayers() {
        synchronized (this){
            while(numberOfPlayers==0){
                try {
                    this.wait();
                } catch (InterruptedException e) {}
            }
            return numberOfPlayers;
        }
    }

    public synchronized void setNumberOfPlayers(int numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
        this.notifyAll();
    }

    public int getAdvancedRules() {
        synchronized (this){
            while(advancedRules==-1){
                try {
                    this.wait();
                } catch (InterruptedException e) {}
            }
            return advancedRules;
        }
    }

    public synchronized void setAdvancedRules(int advancedRules) {
        this.advancedRules = advancedRules;
        this.notifyAll();
    }

    public synchronized GamePhases getPhase() {
        return phase;
    }

    public synchronized void setPhase(GamePhases phase) {
        this.phase = phase;
    }

    public void getCreationPhaseEnded() {
        synchronized (this) {
            while (!creationPhaseEnded) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                }
            }
        }
    }

    public synchronized void setCreationPhaseEnded() {
        this.creationPhaseEnded = true;
        this.notifyAll();
    }
}

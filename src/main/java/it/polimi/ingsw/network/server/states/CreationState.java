package it.polimi.ingsw.network.server.states;

import it.polimi.ingsw.controller.enums.GamePhases;

public class CreationState {
    private int numberOfPlayers;
    private boolean set = false;
    private int advancedRules;
    private GamePhases phase;

    public CreationState() {
        this.numberOfPlayers = 0;
        this.advancedRules = -1;
        this.phase = GamePhases.CREATION_NUMBER_OF_PLAYERS;
    }

    public int getNumberOfPlayers() {
        synchronized (this) {
            while (!set) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                }
            }
            return numberOfPlayers;
        }
    }

    public synchronized void setNumberOfPlayers(int numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
        this.set = true;
        this.notifyAll();
    }

    public void reset() {
        this.set = false;
    }

    public int getAdvancedRules() {
        synchronized (this) {
            while (!set) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                }
            }
            return advancedRules;
        }
    }

    public synchronized void setAdvancedRules(int advancedRules) {
        this.advancedRules = advancedRules;
        this.set = true;
        this.notifyAll();
    }

    public synchronized GamePhases getPhase() {
        return phase;
    }

    public synchronized void setPhase(GamePhases phase) {
        this.phase = phase;
    }

}

package it.polimi.ingsw.network.server.states;

import it.polimi.ingsw.controller.enums.GamePhases;

public class CreationState {
    private int numberOfPlayers;
    private boolean set = false;
    private int advancedRules;
    private GamePhases phase;

    /**
     * Used to keep the game infos during the creation phase
     */
    public CreationState() {
        this.numberOfPlayers = 0;
        this.advancedRules = -1;
        this.phase = GamePhases.CREATION_NUMBER_OF_PLAYERS;
    }

    /**
     *
     * @return is the number of player required to play the game
     */
    public int getNumberOfPlayers() {
        synchronized (this) {
            while (!set) {
                try {
                    this.wait();
                } catch (InterruptedException ignore) {
                }
            }
            return numberOfPlayers;
        }
    }

    /**
     * Used to set the number of player that the game needs to have
     * @param numberOfPlayers is the given number of players
     */
    public synchronized void setNumberOfPlayers(int numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
        this.set = true;
        this.notifyAll();
    }

    /**
     * Used to reset the creation state
     */
    public void reset() {
        this.set = false;
    }

    /**
     *
     * @return is whether the game has advanced rules
     */
    public int getAdvancedRules() {
        synchronized (this) {
            while (!set) {
                try {
                    this.wait();
                } catch (InterruptedException ignore) {
                }
            }
            return advancedRules;
        }
    }

    /**
     * Used to set the type of rules the game has
     * @param advancedRules is the given type of rules
     */
    public synchronized void setAdvancedRules(int advancedRules) {
        this.advancedRules = advancedRules;
        this.set = true;
        this.notifyAll();
    }

    /**
     *
     * @return is the current game phase
     */
    public synchronized GamePhases getPhase() {
        return phase;
    }

    /**
     * Used to set the game phase to the given one
     * @param phase is the given phase
     */
    public synchronized void setPhase(GamePhases phase) {
        this.phase = phase;
    }

}

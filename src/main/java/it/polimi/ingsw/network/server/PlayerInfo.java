package it.polimi.ingsw.network.server;

import it.polimi.ingsw.model.enums.Color;
import it.polimi.ingsw.model.enums.Wizard;

public class PlayerInfo {
    private String username;
    private Color color;
    private Wizard wizard;

    /**
     * Used for graphical purposes
     */
    public PlayerInfo() {

    }

    /**
     *
     * @return is the player's username
     */
    public String getUsername() {
        return username;
    }

    /**
     *
     * @param username is the given username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     *
     * @return is the player's color
     */
    public Color getColor() {
        return color;
    }

    /**
     *
     * @param color is the given color
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     *
     * @return is the player's wizard
     */
    public Wizard getWizard() {
        return wizard;
    }

    /**
     *
     * @param wizard is the given wizard
     */
    public void setWizard(Wizard wizard) {
        this.wizard = wizard;
    }
}

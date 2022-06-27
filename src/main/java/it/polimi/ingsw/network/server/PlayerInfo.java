package it.polimi.ingsw.network.server;

import it.polimi.ingsw.model.enums.Color;
import it.polimi.ingsw.model.enums.Wizard;

public class PlayerInfo {
    private String username;
    private Color color;
    private Wizard wizard;

    public PlayerInfo() {

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Wizard getWizard() {
        return wizard;
    }

    public void setWizard(Wizard wizard) {
        this.wizard = wizard;
    }
}

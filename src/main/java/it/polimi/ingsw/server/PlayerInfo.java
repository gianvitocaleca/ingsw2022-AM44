package it.polimi.ingsw.server;

import it.polimi.ingsw.server.model.enums.*;

public class PlayerInfo{
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

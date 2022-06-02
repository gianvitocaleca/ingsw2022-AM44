package it.polimi.ingsw.server.states;

import it.polimi.ingsw.server.model.enums.Color;
import it.polimi.ingsw.server.model.enums.Wizard;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class LoginState {

    private final Map<Socket, String> usernamesMap;

    private final Map<Socket, Color> colorMap;

    private final Map<Socket, Wizard> wizardMap;

    public LoginState() {
        usernamesMap = new HashMap<>();
        colorMap = new HashMap<>();
        wizardMap = new HashMap<>();
    }

    public String getUsername(Socket socket) {
        synchronized (this.usernamesMap) {
            while (!this.usernamesMap.containsKey(socket)) {
                try {
                    this.usernamesMap.wait();
                } catch (InterruptedException ignore) {
                }
            }
            return this.usernamesMap.get(socket);
        }
    }

    public void removeUsername(Socket socket) {
        this.usernamesMap.remove(socket);
    }

    public void setUsername(Socket socket, String username) {
        synchronized (this.usernamesMap){
            this.usernamesMap.put(socket, username);
            this.usernamesMap.notifyAll();
        }
    }

    public Color getColor(Socket socket) {
        synchronized (this.colorMap) {
            while (!this.colorMap.containsKey(socket)) {
                try {
                    this.colorMap.wait();
                } catch (InterruptedException ignore) {
                }
            }
            return this.colorMap.get(socket);
        }
    }

    public void removeColor(Socket socket) {
        this.colorMap.remove(socket);
    }

    public void setColor(Socket socket, Color color) {
        synchronized (this.colorMap){
            this.colorMap.put(socket, color);
            this.colorMap.notifyAll();
        }
    }

    public Wizard getWizard(Socket socket) {
        synchronized (this.wizardMap) {
            while (!this.wizardMap.containsKey(socket)) {
                try {
                    this.wizardMap.wait();
                } catch (InterruptedException e) {
                }
            }
            return this.wizardMap.get(socket);
        }
    }

    public void removeWizard(Socket socket) {
        this.wizardMap.remove(socket);
    }

    public void setWizard(Socket socket, Wizard wizard) {
        synchronized (this.wizardMap){
            this.wizardMap.put(socket, wizard);
            this.wizardMap.notifyAll();
        }
    }
}

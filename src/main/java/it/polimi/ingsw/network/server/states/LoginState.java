package it.polimi.ingsw.network.server.states;

import it.polimi.ingsw.model.enums.Color;
import it.polimi.ingsw.model.enums.Wizard;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class LoginState {

    private final Map<Socket, String> usernamesMap;

    private final Map<Socket, Color> colorMap;

    private final Map<Socket, Wizard> wizardMap;

    /**
     * Used to keep track of all the player's info during login phase
     */
    public LoginState() {
        usernamesMap = new HashMap<>();
        colorMap = new HashMap<>();
        wizardMap = new HashMap<>();
    }

    /**
     * Used to get the username by the player's socket
     * @param socket is the given socket
     * @return is the username matching the given socket
     */
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

    /**
     * Used to remove the username matching the given socket
     * @param socket is the socket to be removed
     */
    public void removeUsername(Socket socket) {
        this.usernamesMap.remove(socket);
    }

    /**
     * Used to set the username for a given socket
     * @param socket is the given socket
     * @param username is the given username
     */
    public void setUsername(Socket socket, String username) {
        synchronized (this.usernamesMap) {
            this.usernamesMap.put(socket, username);
            this.usernamesMap.notifyAll();
        }
    }

    /**
     * Used to get the color of the given socket
     * @param socket is the given socket
     * @return is the color matching the given socket
     */
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

    /**
     * Used to remove the color matching the given socket
     * @param socket is the socket to be removed
     */
    public void removeColor(Socket socket) {
        this.colorMap.remove(socket);
    }

    /**
     * Used to set the color for a given socket
     * @param socket is the given socket
     * @param color is the given username
     */
    public void setColor(Socket socket, Color color) {
        synchronized (this.colorMap) {
            this.colorMap.put(socket, color);
            this.colorMap.notifyAll();
        }
    }

    /**
     * Used to get the wizard of the given socket
     * @param socket is the given socket
     * @return is the wizard matching the given socket
     */
    public Wizard getWizard(Socket socket) {
        synchronized (this.wizardMap) {
            while (!this.wizardMap.containsKey(socket)) {
                try {
                    this.wizardMap.wait();
                } catch (InterruptedException ignore) {
                }
            }
            return this.wizardMap.get(socket);
        }
    }

    /**
     * Used to remove the wizard matching the given socket
     * @param socket is the socket to be removed
     * @return true if there is a match with the key
     */
    public boolean removeWizard(Socket socket) {
        if(wizardMap.remove(socket)!=null){
            return true;
        }
        return false;
    }

    /**
     * Used to set the wizard for a given socket
     * @param socket is the given socket
     * @param wizard is the given username
     */
    public void setWizard(Socket socket, Wizard wizard) {
        synchronized (this.wizardMap) {
            this.wizardMap.put(socket, wizard);
            this.wizardMap.notifyAll();
        }
    }
}

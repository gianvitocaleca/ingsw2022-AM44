package it.polimi.ingsw.network.server.states;

import it.polimi.ingsw.network.server.enums.ServerPhases;
import it.polimi.ingsw.network.server.PlayerInfo;
import it.polimi.ingsw.network.server.SocketID;
import it.polimi.ingsw.model.enums.Color;
import it.polimi.ingsw.model.enums.Wizard;

import java.net.Socket;
import java.util.*;

public class NetworkState {

    private final List<SocketID> socketIDList;
    private ServerPhases serverPhase;
    private int numberOfPlayers;
    private boolean advancedRules;
    private int loginPhaseEnded = 0;
    private boolean creationPhaseEnded = false;

    /**
     * Keeps track of the number and sockets of all the players
     * @param phase is the phase in which the server is
     */
    public NetworkState(ServerPhases phase) {
        socketIDList = new ArrayList<>();
        this.numberOfPlayers = 1;
        this.advancedRules = false;
        setServerPhase(phase);
    }

    /**
     * Used when a player has finished the login phase
     */
    public synchronized void setLoginPhaseEnded() {
        loginPhaseEnded++;
        this.notifyAll();
    }

    /**
     * Used to check if all the players joined the game
     * @return
     */
    public boolean getLoginPhaseEnded() {
        synchronized (this) {
            while (loginPhaseEnded != numberOfPlayers) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            serverPhase = ServerPhases.GAME;
            return true;
        }
    }

    /**
     *
     * @return is the list of all the players sockets
     */
    public List<SocketID> getSocketIDList() {
        return socketIDList;
    }

    /**
     *
     * @return is the infos of all the connected players
     */
    public List<PlayerInfo> getConnectedPlayerInfo() {
        return socketIDList.stream().filter(SocketID::isConnected).map(SocketID::getPlayerInfo).toList();
    }

    /**
     * Used to register the player's username
     * @param id is the player's id
     * @param username is the player's name
     * @return whether the operation was successful
     */
    public synchronized boolean setUsername(int id, String username) {
        if (isPlayerConnected(username)) return false;
        connectUsername(id, username);
        return true;
    }

    /**
     * Used to register the player's color
     * @param id is the player's id
     * @param color is the player's color
     * @return whether the operation was successful
     */
    public synchronized boolean setColor(int id, Color color) {
        if (isColorTaken(color)) {
            return false;
        }
        paintPlayer(id, color);
        return true;
    }

    /**
     * Used to check if a color was already chosen
     * @param color the color to check
     * @return whether it's already been chosen
     */
    private boolean isColorTaken(Color color) {
        for (SocketID s : socketIDList) {
            if (s.getPlayerInfo().getColor() != null && s.getPlayerInfo().getColor().equals(color)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Used to register the player's wizard
     * @param id is the player's id
     * @param wizard is the player's wizard
     * @return whether the operation was successful
     */
    public synchronized boolean setWizard(int id, Wizard wizard) {
        if (isWizardTaken(wizard)) {
            return false;
        }
        addWitcher(id, wizard);
        return true;
    }

    /**
     * Used to check if a wizard was already chosen
     * @param wizard the wizard to check
     * @return whether it's already been chosen
     */
    private boolean isWizardTaken(Wizard wizard) {
        for (SocketID s : socketIDList) {
            if (s.getPlayerInfo().getWizard() != null && s.getPlayerInfo().getWizard().equals(wizard)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Used to check if a players has left the game
     * @param username of the player to check
     * @return whether the player is still in the game
     */
    public synchronized boolean isPlayerConnected(String username) {
        for (SocketID s : socketIDList) {
            if (s.getPlayerInfo().getUsername() != null && s.getPlayerInfo().getUsername().equals(username) && s.isConnected()) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @return is the number of players still in the game
     */
    public synchronized int getNumberOfConnectedSocket() {
        return socketIDList.stream().filter(SocketID::isConnected).toList().size();
    }

    /**
     * Used to set the username to a specific id
     * @param id the id to give the username
     * @param username the given username
     */
    private void connectUsername(int id, String username) {
        for (SocketID s : socketIDList) {
            if (s.getId() == id) {
                s.getPlayerInfo().setUsername(username);
            }
        }
    }

    /**
     * Used to set the color to a specific id
     * @param id the id to give the color
     * @param color the given color
     */
    private void paintPlayer(int id, Color color) {
        for (SocketID s : socketIDList) {
            if (s.getId() == id) {
                s.getPlayerInfo().setColor(color);
            }
        }
    }

    /**
     * Used to set the wizard to a specific id
     * @param id the id to give the wizard
     * @param wizard the given wizard
     */
    private void addWitcher(int id, Wizard wizard) {
        for (SocketID s : socketIDList) {
            if (s.getId() == id) {
                s.getPlayerInfo().setWizard(wizard);
            }
        }
    }

    /**
     * Used to disconnect a player
     * @param id is the player's id
     */
    public synchronized void disconnectPlayer(int id) {
        for (SocketID s : socketIDList) {
            if (s.getId() == id) {
                s.setConnected(false);
            }
        }
        if (serverPhase.equals(ServerPhases.GAME)) {
            serverPhase = ServerPhases.WAITING;
        }

    }

    /**
     * Used to reconnect a player
     * @param socketID is the new player's socket
     * @return whether the process has finished
     */
    public synchronized boolean reconnectPlayer(SocketID socketID) {

        for (int i = 0; i < socketIDList.size(); i++) {
            SocketID s = socketIDList.get(i);
            if (!s.isConnected()) {
                socketID.setPlayerInfo(s.getPlayerInfo());
                socketIDList.remove(i);
                socketIDList.add(socketID);
                if (getNumberOfConnectedSocket() == numberOfPlayers) {
                    serverPhase = ServerPhases.GAME;
                }
                break;
            }
        }
        return true;
    }

    /**
     * Used to add a new socket
     * @param socketID is the new socket
     */
    public synchronized void addSocket(SocketID socketID) {
        if (!socketIDList.contains(socketID)) {
            socketIDList.add(socketID);
        }
    }

    /**
     *
     * @return is the list of connected sockets
     */
    public synchronized List<Socket> getActiveSockets() {
        return socketIDList.stream().filter(SocketID::isConnected).map(s -> s.getSocket()).toList();
    }

    /**
     *
     * @return is the number of the connected players
     */
    public synchronized int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    /**
     * Used to set the number of players for the game
     * @param numberOfPlayers is the given number of players
     */
    public synchronized void setNumberOfPlayers(int numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
    }

    /**
     *
     * @return whether the game uses advanced rules
     */
    public synchronized boolean isAdvancedRules() {
        return advancedRules;
    }

    /**
     * Used to set the type of rules for the game
     * @param advancedRules whether the game has advanced rules
     */
    public synchronized void setAdvancedRules(boolean advancedRules) {
        this.advancedRules = advancedRules;
    }

    /**
     *
     * @return is the current server phase
     */
    public synchronized ServerPhases getServerPhase() {
        return serverPhase;
    }

    /**
     *
     * @param serverPhase is the phase to be set
     */
    public synchronized void setServerPhase(ServerPhases serverPhase) {
        this.serverPhase = serverPhase;
    }

    /**
     * Used to get the socket given the id of a player
     * @param id is the given id
     * @return is the socket matching the given id
     */
    public synchronized Socket getSocketByID(int id) {
        Socket socket = new Socket();
        for (SocketID s : socketIDList) {
            if (s.getId() == id) {
                socket = s.getSocket();
            }
        }
        return socket;
    }

}


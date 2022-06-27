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

    public NetworkState(ServerPhases phase) {
        socketIDList = new ArrayList<>();
        this.numberOfPlayers = 1;
        this.advancedRules = false;
        setServerPhase(phase);
    }

    public synchronized void setLoginPhaseEnded() {
        loginPhaseEnded++;
        this.notifyAll();
    }

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

    public List<SocketID> getSocketIDList() {
        return socketIDList;
    }

    public List<PlayerInfo> getConnectedPlayerInfo() {
        return socketIDList.stream().filter(SocketID::isConnected).map(SocketID::getPlayerInfo).toList();
    }


    public synchronized boolean setUsername(int id, String username) {
        if (isPlayerConnected(username)) return false;
        connectUsername(id, username);
        return true;
    }

    public synchronized boolean setColor(int id, Color color) {
        if (isColorTaken(color)) {
            return false;
        }
        paintPlayer(id, color);
        return true;
    }

    private boolean isColorTaken(Color color) {
        for (SocketID s : socketIDList) {
            if (s.getPlayerInfo().getColor() != null && s.getPlayerInfo().getColor().equals(color)) {
                return true;
            }
        }
        return false;
    }

    public synchronized boolean setWizard(int id, Wizard wizard) {
        if (isWizardTaken(wizard)) {
            return false;
        }
        addWitcher(id, wizard);
        return true;
    }

    private boolean isWizardTaken(Wizard wizard) {
        for (SocketID s : socketIDList) {
            if (s.getPlayerInfo().getWizard() != null && s.getPlayerInfo().getWizard().equals(wizard)) {
                return true;
            }
        }
        return false;
    }

    public synchronized boolean isPlayerConnected(String username) {
        for (SocketID s : socketIDList) {
            if (s.getPlayerInfo().getUsername() != null && s.getPlayerInfo().getUsername().equals(username) && s.isConnected()) {
                return true;
            }
        }
        return false;
    }

    public synchronized int getNumberOfConnectedSocket() {
        return socketIDList.stream().filter(SocketID::isConnected).toList().size();
    }

    private void connectUsername(int id, String username) {
        for (SocketID s : socketIDList) {
            if (s.getId() == id) {
                s.getPlayerInfo().setUsername(username);
            }
        }
    }

    private void paintPlayer(int id, Color color) {
        for (SocketID s : socketIDList) {
            if (s.getId() == id) {
                s.getPlayerInfo().setColor(color);
            }
        }
    }

    private void addWitcher(int id, Wizard wizard) {
        for (SocketID s : socketIDList) {
            if (s.getId() == id) {
                s.getPlayerInfo().setWizard(wizard);
            }
        }
    }

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

    public synchronized void disconnectSocketId(int id) {
        socketIDList.removeIf(s -> s.getId() == id);
    }

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

    public synchronized void addSocket(SocketID socketID) {
        if (!socketIDList.contains(socketID)) {
            socketIDList.add(socketID);
        }
    }


    public synchronized List<Socket> getActiveSockets() {
        return socketIDList.stream().filter(SocketID::isConnected).map(s -> s.getSocket()).toList();
    }

    public synchronized int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public synchronized void setNumberOfPlayers(int numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
    }

    public synchronized boolean isAdvancedRules() {
        return advancedRules;
    }

    public synchronized void setAdvancedRules(boolean advancedRules) {
        this.advancedRules = advancedRules;
    }

    public synchronized ServerPhases getServerPhase() {
        return serverPhase;
    }

    public synchronized void setServerPhase(ServerPhases serverPhase) {
        this.serverPhase = serverPhase;
    }

    public synchronized Socket getSocketByID(int id) {
        Socket socket = new Socket();
        for (SocketID s : socketIDList) {
            if (s.getId() == id) {
                socket = s.getSocket();
            }
        }
        return socket;
    }

    public int getNumberOfConnectedPlayers() {
        return socketIDList.stream().filter(SocketID::isConnected).map(SocketID::getSocket).toList().size();
    }

}


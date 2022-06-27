package it.polimi.ingsw.view;

import it.polimi.ingsw.model.enums.Name;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.network.server.networkMessages.Headers;
import it.polimi.ingsw.network.server.networkMessages.payloads.ShowModelPayload;

public class ClientState {
    private Headers headers;
    private String username;

    private boolean currentPlayer = true;

    private ShowModelPayload modelPayload;

    private boolean moveStudents = false;
    private boolean moveMotherNature = false;
    private boolean selectCloud;
    private boolean selectCharacter;
    private boolean setDisconnection;
    private boolean disconnection;
    private Name currentPlayedCharacter;


    public ClientState() {
        this.headers = Headers.creationRequirementMessage;
        this.setDisconnection = false;
        this.disconnection = false;
    }

    public boolean getDisconnection() {
        while (!setDisconnection) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return disconnection;
    }

    public void setDisconnection(Headers header) {
        synchronized (this) {
            if (header.equals(Headers.closeConnection)) {
                disconnection = true;
            }
            setDisconnection = true;
        }
    }

    public Headers getHeaders() {
        return headers;
    }

    public void setHeaders(Headers headers) {
        this.headers = headers;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setShowModel(ShowModelPayload payload) {

        if (payload.isUpdateAll()) {
            modelPayload = payload;
        }
        if (payload.isUpdateClouds()) {
            modelPayload.setClouds(payload.getClouds());
        }
        if (payload.isUpdateCoinReserve()) {
            modelPayload.setCoinReserve(payload.getCoinReserve());
            for (Player p : modelPayload.getPlayersList()) {
                if (p.getUsername().equals(modelPayload.getCurrentPlayerUsername())) {
                    p.setMyCoins(payload.getCurrentPlayerCoins());
                }
            }
        }
        if (payload.isUpdateIslands()) {
            modelPayload.setIslands(payload.getIslands());
            modelPayload.setDeactivators(payload.getDeactivators());
            for (Player p : modelPayload.getPlayersList()) {
                p.setTowers(payload.getPlayerTowers(p.getUsername()));
            }

        }
        if (payload.isUpdateMotherNature()) {
            modelPayload.setMotherNature(payload.getMotherNature());
        }
        if (payload.isUpdatePlayersDiningRoom()) {
            modelPayload.setCoinReserve(payload.getCoinReserve());
            modelPayload.setPlayersList(payload.getPlayersList());
        } else if (payload.isUpdatePlayersEntrance()) {
            for (int i = 0; i < modelPayload.getPlayersList().size(); i++) {
                modelPayload.getPlayersList().get(i).setEntrance(payload.getPlayersList().get(i).getEntrance());
            }
        }
        if (payload.isUpdatePlayersAssistant()) {
            for (int i = 0; i < payload.getPlayersList().size(); i++) {
                if (payload.getPlayersList().get(i).getUsername().equals(payload.getCurrentPlayerUsername())) {
                    modelPayload.getPlayersList().get(i).setLastPlayedCards(payload.getPlayersList().get(i).getLastPlayedCards());
                    modelPayload.getPlayersList().get(i).setAssistantDeck(payload.getPlayersList().get(i).getAssistantDeck());
                }
            }
        }
        if (payload.isUpdatePlayedCharacter()) {
            modelPayload.setCoinReserve(payload.getCoinReserve());
            for (Player p : modelPayload.getPlayersList()) {
                if (p.getUsername().equals(modelPayload.getCurrentPlayerUsername())) {
                    p.setMyCoins(payload.getCurrentPlayerCoins());
                }
            }
            modelPayload.setMonkCreatures(payload.getMonkCreatures());
            modelPayload.setPrincessCreatures(payload.getPrincessCreatures());
            modelPayload.setJokerCreatures(payload.getJokerCreatures());
            modelPayload.setCharacters(payload.getCharacters());
        }

    }

    public ShowModelPayload getModelPayload() {
        return modelPayload;
    }

    public boolean getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(boolean value) {
        currentPlayer = value;
    }

    public boolean isMoveStudents() {
        return moveStudents;
    }

    public void setMoveStudents(boolean moveStudents) {
        this.moveStudents = moveStudents;
    }

    public boolean isMoveMotherNature() {
        return moveMotherNature;
    }

    public void setMoveMotherNature(boolean moveMotherNature) {
        this.moveMotherNature = moveMotherNature;
    }

    public boolean isSelectCloud() {
        return selectCloud;
    }

    public void setSelectCloud(boolean selectCloud) {
        this.selectCloud = selectCloud;
    }

    public boolean isSelectCharacter() {
        return selectCharacter;
    }

    public void setSelectCharacter(boolean selectCharacter) {
        this.selectCharacter = selectCharacter;
    }

    public Name getCurrentPlayedCharacter() {
        return currentPlayedCharacter;
    }

    public void setCurrentPlayedCharacter(Name character) {
        currentPlayedCharacter = character;
    }
}

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

    /**
     * Class used to keep track of the player connection status and infos
     */
    public ClientState() {
        this.headers = Headers.creationRequirementMessage;
        this.setDisconnection = false;
        this.disconnection = false;
    }

    /**
     *
     * @return is true when the player has disconnected
     */
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

    /**
     * Determines the state of the player by the given header
     * @param header is the given header
     */
    public void setDisconnection(Headers header) {
        synchronized (this) {
            if (header.equals(Headers.closeConnection)) {
                disconnection = true;
            }
            setDisconnection = true;
        }
    }

    /**
     *
     * @return is the player's header
     */
    public Headers getHeaders() {
        return headers;
    }

    /**
     *
     * @param headers is the header to be set
     */
    public void setHeaders(Headers headers) {
        this.headers = headers;
    }

    /**
     *
     * @return is the player's name
     */
    public String getUsername() {
        return this.username;
    }

    /**
     *
     * @param username is the name to be set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Creates a local copy of the given payload.
     * Used for incremental updates.
     * @param payload contains the information about the game status
     */
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

    /**
     *
     * @return is the current copy of the game
     */
    public ShowModelPayload getModelPayload() {
        return modelPayload;
    }

    /**
     *
     * @return true if the player is the current
     */
    public boolean getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     *
     * @param value is whether the player is the current
     */
    public void setCurrentPlayer(boolean value) {
        currentPlayer = value;
    }

    /**
     *
     * @return is true if students need to be moved
     */
    public boolean isMoveStudents() {
        return moveStudents;
    }

    /**
     *
     * @param moveStudents is whether the students need to be moved
     */
    public void setMoveStudents(boolean moveStudents) {
        this.moveStudents = moveStudents;
    }

    /**
     *
     * @return is true if mother nature needs to be moved
     */
    public boolean isMoveMotherNature() {
        return moveMotherNature;
    }

    /**
     *
     * @param moveMotherNature is whether mother nature needs to be moved
     */
    public void setMoveMotherNature(boolean moveMotherNature) {
        this.moveMotherNature = moveMotherNature;
    }

    /**
     *
     * @return is true if a cloud has to be selected
     */
    public boolean isSelectCloud() {
        return selectCloud;
    }

    /**
     *
     * @param selectCloud is whether a cloud has to be selected
     */
    public void setSelectCloud(boolean selectCloud) {
        this.selectCloud = selectCloud;
    }

    /**
     *
     * @return is true if a character needs to be selected
     */
    public boolean isSelectCharacter() {
        return selectCharacter;
    }

    /**
     *
     * @param selectCharacter is whether a character needs to be selected
     */
    public void setSelectCharacter(boolean selectCharacter) {
        this.selectCharacter = selectCharacter;
    }

    /**
     *
     * @return is the name of the current played character
     */
    public Name getCurrentPlayedCharacter() {
        return currentPlayedCharacter;
    }

    /**
     *
     * @param character is the name of the current played character
     */
    public void setCurrentPlayedCharacter(Name character) {
        currentPlayedCharacter = character;
    }
}

package it.polimi.ingsw.client;

import it.polimi.ingsw.server.model.GameModel;
import it.polimi.ingsw.server.model.ModelCache;
import it.polimi.ingsw.server.model.enums.Name;
import it.polimi.ingsw.server.networkMessages.Headers;
import it.polimi.ingsw.server.networkMessages.ShowModelPayload;

public class ClientState {
    private Headers headers;
    private String username;

    private boolean currentPlayer = true;

    private ShowModelPayload modelCache;

    private boolean moveStudents = false;
    private boolean moveMotherNature = false;
    private boolean selectCloud;
    private boolean selectCharacter;

    private Name currentPlayedCharacter;


    public ClientState() {
        this.headers = Headers.creationRequirementMessage;
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
            modelCache = payload;
        }
        if (payload.isUpdateClouds()) {
            modelCache.setClouds(payload.getClouds());
        }

    }

    public ShowModelPayload getModelCache() {
        return modelCache;
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

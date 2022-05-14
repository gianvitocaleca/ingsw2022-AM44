package it.polimi.ingsw.client;

import it.polimi.ingsw.server.model.GameModel;
import it.polimi.ingsw.server.model.ModelCache;
import it.polimi.ingsw.server.model.enums.Name;
import it.polimi.ingsw.server.model.player.Player;
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
        if (payload.isUpdateCoinReserve()){
            modelCache.setCoinReserve(payload.getCoinReserve());
            for(Player p: modelCache.getPlayersList()){
                if (p.getUsername().equals(modelCache.getCurrentPlayerUsername())){
                    p.setMyCoins(payload.getCurrentPlayerCoins());
                }
            }
        }
        if (payload.isUpdateIslands()){
            modelCache.setIslands(payload.getIslands());
            modelCache.setDeactivators(payload.getDeactivators());
            for(Player p: modelCache.getPlayersList()){
                p.setTowers(payload.getPlayerTowers(p.getUsername()));
            }

        }
        if (payload.isUpdateMotherNature()){
            modelCache.setMotherNature(payload.getMotherNature());
        }
        if (payload.isUpdatePlayersDiningRoom()){
            modelCache.setCoinReserve(payload.getCoinReserve());
            modelCache.setPlayersList(payload.getPlayersList());
        }else if (payload.isUpdatePlayersEntrance()){
            for(int i = 0; i<modelCache.getPlayersList().size(); i++){
                modelCache.getPlayersList().get(i).setEntrance(payload.getPlayersList().get(i).getEntrance());
            }
        }
        if (payload.isUpdatePlayersAssistant()){
            for(int i = 0; i<payload.getPlayersList().size();i++){
                if(payload.getPlayersList().get(i).getUsername().equals(payload.getCurrentPlayerUsername())){
                    modelCache.getPlayersList().get(i).setLastPlayedCards(payload.getPlayersList().get(i).getLastPlayedCards());
                    modelCache.getPlayersList().get(i).setAssistantDeck(payload.getPlayersList().get(i).getAssistantDeck());
                }
            }
        }
        if(payload.isUpdatePlayedCharacter()){
            modelCache.setCoinReserve(payload.getCoinReserve());
            for(Player p: modelCache.getPlayersList()){
                if (p.getUsername().equals(modelCache.getCurrentPlayerUsername())){
                    p.setMyCoins(payload.getCurrentPlayerCoins());
                }
            }
            modelCache.setCharacters(payload.getCharacters());
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

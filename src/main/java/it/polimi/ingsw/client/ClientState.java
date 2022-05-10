package it.polimi.ingsw.client;

import it.polimi.ingsw.server.model.GameModel;
import it.polimi.ingsw.server.networkMessages.Headers;
import it.polimi.ingsw.server.networkMessages.ShowModelPayload;

public class ClientState {
    private Headers headers;
    private String username;

    private ShowModelPayload modelCache;

    public ClientState() {
        this.headers = Headers.creationRequirementMessage;
    }

    public Headers getHeaders() {
        return headers;
    }

    public void setHeaders(Headers headers) {
        this.headers = headers;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public String getUsername(){
        return this.username;
    }

    public void setShowModel(ShowModelPayload payload){
        modelCache = payload;
    }

    public ShowModelPayload getModelCache() {
        return modelCache;
    }
}

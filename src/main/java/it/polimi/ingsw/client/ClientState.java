package it.polimi.ingsw.client;

import it.polimi.ingsw.server.networkMessages.Headers;

public class ClientState {
    Headers headers;

    public ClientState() {
        this.headers = Headers.creationRequirementMessage;
    }

    public Headers getHeaders() {
        return headers;
    }

    public void setHeaders(Headers headers) {
        this.headers = headers;
    }
}

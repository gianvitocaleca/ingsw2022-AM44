package it.polimi.ingsw.network.server.networkMessages;

import it.polimi.ingsw.network.server.networkMessages.payloads.Payload;

public record Message(Headers header, Payload payload) {
}

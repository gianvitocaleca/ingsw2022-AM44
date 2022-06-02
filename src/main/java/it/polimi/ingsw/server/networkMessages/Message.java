package it.polimi.ingsw.server.networkMessages;

import it.polimi.ingsw.server.networkMessages.payloads.Payload;

public record Message (Headers header, Payload payload) {
}

package it.polimi.ingsw.network.server.networkMessages;

import it.polimi.ingsw.network.server.networkMessages.payloads.Payload;

/**
 * Used to create the message to send on the network
 * @param header describes the type of payload
 * @param payload contains the necessary info
 */
public record Message(Headers header, Payload payload) {
}

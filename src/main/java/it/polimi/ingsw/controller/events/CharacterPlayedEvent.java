package it.polimi.ingsw.controller.events;

import it.polimi.ingsw.model.enums.Name;

import java.net.Socket;
import java.util.EventObject;

/**
 * This event contains the name of the character played by the player.
 */
public class CharacterPlayedEvent extends EventObject {

    private Name charactersName;
    private Socket socket;

    /**
     * Constructs a prototypical Event.
     *
     * @param source the object on which the Event initially occurred
     * @param socket the current player socket
     * @throws IllegalArgumentException if source is null
     */
    public CharacterPlayedEvent(Object source, Name charactersName, Socket socket) {
        super(source);
        this.charactersName = charactersName;
        this.socket = socket;
    }

    public Name getCharactersName() {
        return charactersName;
    }

    public Socket getSocket() {
        return socket;
    }
}

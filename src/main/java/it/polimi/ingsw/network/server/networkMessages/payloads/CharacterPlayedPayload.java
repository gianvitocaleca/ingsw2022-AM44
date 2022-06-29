package it.polimi.ingsw.network.server.networkMessages.payloads;

import it.polimi.ingsw.model.enums.Name;

public class CharacterPlayedPayload implements Payload {
    private Name charactersName;

    /**
     * Contains the name of the played character
     * @param charactersName is the name of the character
     */
    public CharacterPlayedPayload(Name charactersName) {
        this.charactersName = charactersName;
    }

    /**
     *
     * @return is the character's name
     */
    public Name getCharactersName() {
        return charactersName;
    }
}
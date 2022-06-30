package it.polimi.ingsw.server.networkMessages.payloads;

import it.polimi.ingsw.server.model.enums.Name;

public class CharacterPlayedPayload implements Payload {
    private Name charactersName;

    public CharacterPlayedPayload(Name charactersName) {
        this.charactersName = charactersName;
    }

    public Name getCharactersName() {
        return charactersName;
    }
}
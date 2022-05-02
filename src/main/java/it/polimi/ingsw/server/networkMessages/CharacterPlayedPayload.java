package it.polimi.ingsw.server.networkMessages;

import it.polimi.ingsw.server.model.enums.Name;

public class CharacterPlayedPayload implements Payload{
    private Name charactersName;

    public CharacterPlayedPayload(Name charactersName) {
        this.charactersName = charactersName;
    }
}

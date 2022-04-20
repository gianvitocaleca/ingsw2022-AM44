package it.polimi.ingsw.messages;

import it.polimi.ingsw.model.enums.Name;

public class CharacterPlayedPayload implements Payload{
    private Name charactersName;

    public CharacterPlayedPayload(Name charactersName) {
        this.charactersName = charactersName;
    }
}

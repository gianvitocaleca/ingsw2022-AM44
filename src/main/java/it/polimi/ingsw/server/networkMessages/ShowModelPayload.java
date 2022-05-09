package it.polimi.ingsw.server.networkMessages;

import it.polimi.ingsw.server.model.enums.Name;
import it.polimi.ingsw.server.model.gameboard.Table;
import it.polimi.ingsw.server.model.player.Player;
import java.util.List;
import java.util.Map;

public class ShowModelPayload implements Payload{

    List<Player> playersList;
    Table table;
    Map<Name,Boolean> characters;
    Name playedCharacter;

    public ShowModelPayload(List<Player> playersList, Table table, Map<Name, Boolean> characters, Name playedCharacter) {
        this.playersList = playersList;
        this.table = table;
        this.characters = characters;
        this.playedCharacter = playedCharacter;
    }

    public List<Player> getPlayersList() {
        return playersList;
    }

    public Table getTable() {
        return table;
    }

    public Map<Name, Boolean> getCharacters() {
        return characters;
    }

    public Name getPlayedCharacter() {
        return playedCharacter;
    }
}

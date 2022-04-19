package it.polimi.ingsw.characterTests;

import it.polimi.ingsw.model.GameModel;
import it.polimi.ingsw.messages.CharactersParameters;
import it.polimi.ingsw.model.characters.Herbalist;
import it.polimi.ingsw.model.enums.Color;
import it.polimi.ingsw.model.enums.Name;
import it.polimi.ingsw.model.enums.Wizard;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.studentcontainers.Cloud;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HerbalistTest {


    GameModel gm;


    /**
     * This create a new GameModel instance to use in every test
     */
    @BeforeEach
    public void createGameModel() {
        gm = new GameModel(true,
                new ArrayList<>(Arrays.asList("Paolo", "Gianvito", "Sabrina")),
                3,
                new ArrayList<>(Arrays.asList(Color.values())),
                new ArrayList<>(Arrays.asList(Wizard.YELLOW, Wizard.PINK, Wizard.BLUE)));
    }


    /**
     * This test verifies that herbalist's effect has the correct behaviour
     */
    @Test
    void herbalistEffectTest() {
        int islandIndex = new Random().nextInt(gm.getTable().getIslands().size());

        CharactersParameters herbalist = new CharactersParameters(new ArrayList<>(), islandIndex, 0, new Cloud(12), new ArrayList<>());
        //set Herbalist Character in characters to test her effect.
        gm.getCharacters().remove(0);
        gm.getCharacters().add(0, new Herbalist(Name.HERBALIST, gm));

        List<Player> players = gm.getPlayers();
        players.get(gm.getCurrentPlayerIndex()).addCoin();
        players.get(gm.getCurrentPlayerIndex()).addCoin();
        players.get(gm.getCurrentPlayerIndex()).addCoin();
        gm.setPlayers(players);

        gm.playCharacter(0);
        gm.effect(herbalist);
        assertEquals(gm.getTable().getIslands().get(islandIndex).getNumberOfNoEntries(), 1);
    }
}

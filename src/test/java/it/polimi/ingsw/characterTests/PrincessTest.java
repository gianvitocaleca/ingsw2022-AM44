package it.polimi.ingsw.characterTests;

import it.polimi.ingsw.model.GameModel;
import it.polimi.ingsw.model.characters.Character;
import it.polimi.ingsw.model.characters.ConcreteCharacterCreator;
import it.polimi.ingsw.model.enums.Color;
import it.polimi.ingsw.model.enums.Name;
import it.polimi.ingsw.model.enums.Wizard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PrincessTest {

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

    @Test
    void princessEffectTest() {
        //creates the character
        ConcreteCharacterCreator ccc = new ConcreteCharacterCreator();
        Character princess = ccc.createCharacter(Name.PRINCESS, gm);
        //put the character in first position
        gm.getCharacters().remove(0);
        gm.getCharacters().add(0, princess);
        //give coins to player
        gm.getPlayers().get(gm.getCurrentPlayerIndex()).addCoin();
        gm.getPlayers().get(gm.getCurrentPlayerIndex()).addCoin();

        assertEquals(0, 1);
    }
}

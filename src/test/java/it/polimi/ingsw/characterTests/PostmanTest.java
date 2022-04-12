package it.polimi.ingsw.characterTests;

import it.polimi.ingsw.model.GameModel;
import it.polimi.ingsw.model.characters.CharactersParameters;
import it.polimi.ingsw.model.characters.Postman;
import it.polimi.ingsw.model.characters.Thief;
import it.polimi.ingsw.model.enums.Color;
import it.polimi.ingsw.model.enums.Creature;
import it.polimi.ingsw.model.enums.Name;
import it.polimi.ingsw.model.enums.Wizard;
import it.polimi.ingsw.model.students.StudentBucket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;


public class PostmanTest {
    private GameModel gm;

    @BeforeEach
    public void createGameModel() {
        gm = new GameModel(true,
                new ArrayList<>(Arrays.asList("Paolo", "Gianvito", "Sabrina")),
                3,
                new ArrayList<>(Arrays.asList(Color.values())),
                new ArrayList<>(Arrays.asList(Wizard.YELLOW, Wizard.PINK, Wizard.BLUE)));
    }

    @Test
    public void postmanTest() {
        CharactersParameters Postman = new CharactersParameters(new ArrayList<>(),
                0, 2, null, new ArrayList<>());

        gm.getCharacters().remove(0);
        gm.getCharacters().add(0, new Postman(Name.MAGICPOSTMAN, gm));

        gm.getPlayers().get(gm.getCurrentPlayerIndex()).addCoin();
        gm.getPlayers().get(gm.getCurrentPlayerIndex()).addCoin();
        gm.getPlayers().get(gm.getCurrentPlayerIndex()).addCoin();

        gm.playCharacter(0);
        gm.effect(Postman);

        int oldposition = gm.getTable().getMnPosition();

        if (2 < ((gm.getTable().getIslands().size() - 1) - gm.getTable().getMnPosition())) {
            gm.moveMotherNature(0);
            assertTrue(gm.getTable().getMnPosition() == oldposition + 2);
        } else {
            gm.moveMotherNature(0);
            assertTrue(gm.getTable().getMnPosition() == (oldposition + 2) % (gm.getTable().getIslands().size()));
        }

    }

    @Test
    public void setWrongPostmanMovementsTest(){
        CharactersParameters Postman = new CharactersParameters(new ArrayList<>(),
                0, 200, null, new ArrayList<>());

        gm.getCharacters().remove(0);
        gm.getCharacters().add(0, new Postman(Name.MAGICPOSTMAN, gm));

        gm.getPlayers().get(gm.getCurrentPlayerIndex()).addCoin();
        gm.getPlayers().get(gm.getCurrentPlayerIndex()).addCoin();
        gm.getPlayers().get(gm.getCurrentPlayerIndex()).addCoin();

        gm.playCharacter(0);
        assertFalse(gm.effect(Postman));
    }
}

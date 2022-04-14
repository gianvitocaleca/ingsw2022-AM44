package it.polimi.ingsw.characterTests;

import it.polimi.ingsw.model.GameModel;
import it.polimi.ingsw.model.characters.CharactersParameters;
import it.polimi.ingsw.model.characters.Herald;
import it.polimi.ingsw.model.enums.Color;
import it.polimi.ingsw.model.enums.Creature;
import it.polimi.ingsw.model.enums.Name;
import it.polimi.ingsw.model.enums.Wizard;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.player.Professor;
import it.polimi.ingsw.model.studentcontainers.Cloud;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HeraldTest {

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
     * This test verifies that herald has the correct behaviour.
     */
    @Test
    void heraldEffectTest() {
        int islandIndex = new Random().nextInt(gm.getTable().getIslands().size());
        CharactersParameters herald = new CharactersParameters(new ArrayList<>(), islandIndex, 0, new Cloud(12), new ArrayList<>());
        List<Professor> profes = new ArrayList<>();
        for (Creature c : Creature.values()) {
            profes.add(new Professor(c));
        }
        for (int i = 0; i < gm.getPlayers().size(); i++) {
            gm.getPlayers().get(i).addProfessor(profes.get(i));
        }
        gm.getPlayers().get(0).addProfessor(profes.get(3));
        gm.getPlayers().get(0).addProfessor(profes.get(4));
        //set Herald Character in characters to test her effect.
        gm.getCharacters().remove(0);
        gm.getCharacters().add(0, new Herald(Name.HERALD, gm));
        gm.getPlayers().get(gm.getCurrentPlayerIndex()).addCoin();
        gm.getPlayers().get(gm.getCurrentPlayerIndex()).addCoin();
        gm.getPlayers().get(gm.getCurrentPlayerIndex()).addCoin();
        gm.playCharacter(0);
        gm.effect(herald);
        for (Creature c : Creature.values()) {
            if (gm.getTable().getIslands().get(islandIndex).getNumberOfStudentsByCreature(c) == 1) {
                for (Player p : gm.getPlayers()) {
                    for (Professor prof : p.getProfessors()) {
                        if (prof.getCreature().equals(c)) {
                            //player that has influence on the island
                            assertEquals(gm.getTable().getIslands().get(islandIndex).getColorOfTowers(), p.getMyColor());

                        }
                    }
                }
            }
        }
    }
}

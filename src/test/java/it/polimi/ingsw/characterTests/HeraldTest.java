package it.polimi.ingsw.characterTests;

import it.polimi.ingsw.model.exceptions.UnplayableEffectException;
import it.polimi.ingsw.model.exceptions.GameEndedException;
import it.polimi.ingsw.model.GameModel;
import it.polimi.ingsw.network.server.networkMessages.payloads.CharactersParametersPayload;
import it.polimi.ingsw.model.characters.Herald;
import it.polimi.ingsw.model.enums.Color;
import it.polimi.ingsw.model.enums.Creature;
import it.polimi.ingsw.model.enums.Name;
import it.polimi.ingsw.model.enums.Wizard;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.player.Professor;
import it.polimi.ingsw.model.studentcontainers.Island;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * This tests the herald class
 */
public class HeraldTest {

    GameModel gm;


    /**
     * This creates a new GameModel instance to use in every test
     */
    @BeforeEach
    public void createGameModel() {
        gm = new GameModel(true,
                new ArrayList<>(Arrays.asList("Paolo", "Gianvito", "Sabrina")),
                3,
                new ArrayList<>(Arrays.asList(Color.values())),
                new ArrayList<>(Arrays.asList(Wizard.GANDALF, Wizard.SABRINA, Wizard.BALJEET)));
    }

    /**
     * This test verifies that herald has the correct behaviour.
     */
    @Test
    void heraldEffectTest() throws GameEndedException, UnplayableEffectException {
        int islandIndex = new Random().nextInt(gm.getTable().getIslands().size());
        CharactersParametersPayload herald = new CharactersParametersPayload(new ArrayList<>(), islandIndex, 0, new ArrayList<>());
        List<Professor> professors = new ArrayList<>();
        for (Creature c : Creature.values()) {
            professors.add(new Professor(c));
        }
        List<Player> players = gm.getPlayers();

        for (int i = 0; i < gm.getPlayers().size(); i++) {
            players.get(i).addProfessor(professors.get(i));
        }
        players.get(0).addProfessor(professors.get(3));
        players.get(0).addProfessor(professors.get(4));
        //set Herald Character in characters to test his effect.
        gm.getCharacters().remove(0);
        gm.getCharacters().add(0, new Herald(Name.HERALD, gm));
        players.get(gm.getCurrentPlayerIndex()).addCoin();
        players.get(gm.getCurrentPlayerIndex()).addCoin();
        players.get(gm.getCurrentPlayerIndex()).addCoin();
        gm.setPlayers(players);
        gm.playCharacter(0);
        gm.effect(herald);

        List<Island> islands = gm.getTable().getIslands();

        for (Creature c : Creature.values()) {
            if (islands.get(islandIndex).getNumberOfStudentsByCreature(c) == 1) {
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

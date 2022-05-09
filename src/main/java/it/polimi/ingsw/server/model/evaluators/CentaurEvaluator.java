package it.polimi.ingsw.server.model.evaluators;

import it.polimi.ingsw.server.model.GameModel;
import it.polimi.ingsw.server.model.gameboard.Table;
import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.server.model.player.Professor;
import it.polimi.ingsw.server.model.studentcontainers.Island;

import java.util.List;
import java.util.Optional;

//CENTAUR DOES NOT EVALUATE TOWERS
public class CentaurEvaluator implements InfluenceEvaluator {
    @Override
    public void evaluateInfluence(GameModel model) {
        Table table = model.getTable();
        List<Player> players = model.getPlayers();

        Island ci = table.getCurrentIsland();
        if (ci.getNumberOfNoEntries() == 0) {
            Optional<Player> hasmoreinfluece = Optional.empty();
            int influence = 0;
            for (Player p : players) {
                int sum = 0;
                //if player has professor add the relative influence
                if (p.getProfessors().size() > 0) {
                    for (Professor prof : p.getProfessors()) {
                        sum += ci.getNumberOfStudentsByCreature(prof.getCreature());
                    }
                }
                //if player has more influence update
                if (sum > influence) {
                    hasmoreinfluece = Optional.of(p);
                    influence = sum;
                }
            }
            //if the player who has more influence has changed
            if(hasmoreinfluece.isPresent()){
                model.conquerIsland(hasmoreinfluece.get());
            }

        } else {
            ci.removeNoEntry();
        }

    }
}

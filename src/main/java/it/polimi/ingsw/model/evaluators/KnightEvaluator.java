package it.polimi.ingsw.model.evaluators;

import it.polimi.ingsw.model.GameModel;
import it.polimi.ingsw.model.evaluators.InfluenceEvaluator;
import it.polimi.ingsw.model.gameboard.Table;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.player.Professor;
import it.polimi.ingsw.model.studentcontainers.Island;

import java.util.List;
import java.util.Optional;

public class KnightEvaluator implements InfluenceEvaluator {
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
                //add +2 to influence of knight card to current player
                if(p.equals(players.get(model.getCurrentPlayerIndex()))){
                    sum+=2;
                }
                //if player has professor add the relative influence
                if (p.getProfessors().size() > 0) {
                    for (Professor prof : p.getProfessors()) {
                        sum += ci.getNumberOfStudentsByCreature(prof.getCreature());
                    }
                }
                //if player has towers on the island add the relative influence
                if (ci.getNumberOfTowers() > 0 &&
                        p.getMyColor().equals(ci.getColorOfTowers())) {
                    sum += ci.getNumberOfTowers();
                }
                //if player has more influence update
                if (sum > influence) {
                    hasmoreinfluece = Optional.of(p);
                    influence = sum;
                }
            }
            //if the player who has more influence has changed
            model.conquerIsland(hasmoreinfluece);
        } else {
            ci.removeNoEntry();
        }

    }
}

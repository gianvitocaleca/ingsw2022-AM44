package it.polimi.ingsw.model;

import it.polimi.ingsw.model.gameboard.Table;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.player.Professor;
import it.polimi.ingsw.model.studentcontainers.Island;

import java.util.List;
import java.util.Optional;

public class StandardEvaluator implements InfluenceEvaluator {
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
            if (!hasmoreinfluece.get().getMyColor().equals(ci.getColorOfTowers())) {
                //swap towers
                if (ci.getNumberOfTowers() > 0) {
                    for (Player p : players) {
                        //removes towers from the player who has influence
                        if (p.getMyColor().equals(hasmoreinfluece.get().getMyColor())) {
                            p.removeTowers(ci.getNumberOfTowers());
                        }
                        //adds towers to the player who had towers on the island
                        if (p.getMyColor().equals(ci.getColorOfTowers())) {
                            p.addTowers(ci.getNumberOfTowers());
                        }
                    }
                } else {
                    //removes one tower from the player that has conquered the island
                    for (Player p : players) {
                        if (p.getMyColor().equals(hasmoreinfluece.get().getMyColor())) {
                            p.removeTowers(1);
                        }
                    }
                }
                //change the color of the towers on the island
                ci.setColorOfTowers(hasmoreinfluece.get().getMyColor());
                //check the neighbor islands
                model.checkNeighborIsland();
            }
        } else {
            ci.removeNoEntry();
        }

    }
}

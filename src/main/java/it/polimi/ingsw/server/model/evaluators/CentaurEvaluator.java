package it.polimi.ingsw.server.model.evaluators;

import it.polimi.ingsw.server.model.player.Player;

//CENTAUR DOES NOT EVALUATE TOWERS
public class CentaurEvaluator extends InfluenceEvaluator {
    @Override
    public boolean evaluation(){
        if(currentIsland.getNumberOfNoEntries()==0){
            for(Player p: players){
                currentInfluence = 0;
                evaluateProfessors(p);
                influences.add(currentInfluence);
                influencesPerUsername.put(currentInfluence,p.getUsername());
            }
            return true;
        }else{
            currentIsland.removeNoEntry();
            table.setCurrentIsland(currentIsland);
            return false;
        }
    }
}

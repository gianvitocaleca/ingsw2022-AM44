package it.polimi.ingsw.model.evaluators;

import it.polimi.ingsw.model.player.Player;

public class StandardEvaluator extends InfluenceEvaluator {

    public boolean evaluation(){
        if(currentIsland.getNumberOfNoEntries()==0){
            for(Player p: players){
                currentInfluence = 0;
                evaluateProfessors(p);
                evaluateTowers(p);
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

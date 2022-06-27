package it.polimi.ingsw.model.evaluators;

import it.polimi.ingsw.model.enums.Creature;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.player.Professor;

public class FungaroEvaluator extends InfluenceEvaluator {
    private final Creature creature;

    public FungaroEvaluator(Creature creature) {
        this.creature = creature;
    }

    public boolean evaluation(){
        if(currentIsland.getNumberOfNoEntries()==0){
            for(Player p: players){
                currentInfluence = 0;
                if(p.getProfessors().size()>0){
                    for(Professor professor: p.getProfessors()){
                        if (!(professor.getCreature().equals(creature))) {
                            currentInfluence += currentIsland.getNumberOfStudentsByCreature(professor.getCreature());
                        }
                    }
                }
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

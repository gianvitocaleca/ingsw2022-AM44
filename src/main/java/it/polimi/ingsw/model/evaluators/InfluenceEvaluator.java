package it.polimi.ingsw.model.evaluators;

import it.polimi.ingsw.model.GameModel;
import it.polimi.ingsw.model.exceptions.GameEndedException;
import it.polimi.ingsw.model.studentcontainers.Island;
import it.polimi.ingsw.model.gameboard.Table;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.player.Professor;

import java.util.*;

public abstract class InfluenceEvaluator {
    protected Table table;
    protected List<Player> players;
    protected Map<Integer,String> influencesPerUsername;
    protected List<Integer> influences;
    protected Island currentIsland;
    protected int currentInfluence;
    protected GameModel model;

    public void evaluateInfluence(GameModel model) throws GameEndedException {
        table = model.getTable();
        players = model.getPlayers();
        influencesPerUsername = new HashMap<>();
        influences = new ArrayList<>();
        currentIsland = table.getCurrentIsland();
        currentInfluence = 0;
        this.model = model;

        if (evaluation()) {
            influences.sort(Comparator.reverseOrder());
            if(!Objects.equals(influences.get(0), influences.get(1))){
                for(Player p: players){
                    if(p.getUsername().equals(influencesPerUsername.get(influences.get(0)))){
                        model.conquerIsland(p);
                    }
                }
            }
        }else{
            model.setTable(table);
        }

    }

    abstract boolean evaluation();

    protected void evaluateProfessors(Player p){
        if(p.getProfessors().size()>0){
            for(Professor professor: p.getProfessors()){
                currentInfluence += currentIsland.getNumberOfStudentsByCreature(professor.getCreature());
            }
        }
    }

    protected void evaluateTowers(Player p){
        if(currentIsland.getNumberOfTowers()>0 && p.getMyColor().equals(currentIsland.getColorOfTowers())){
            currentInfluence += currentIsland.getNumberOfTowers();
        }
    }
}

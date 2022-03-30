package it.polimi.ingsw.model.gameboard;

import it.polimi.ingsw.model.exceptions.GroupsOfIslandsException;
import it.polimi.ingsw.model.studentcontainers.Cloud;
import it.polimi.ingsw.model.studentcontainers.Island;
import it.polimi.ingsw.model.students.Student;

import java.util.*;

public class Table {
    private final List<Island> islands;
    private final List<Cloud> clouds;
    private final MotherNature motherNature;
    private int coinReserve;

    /**
     * The constructor makes a new table with provided parameters
     * @param islands is the list of islands created by GameModel
     * @param clouds is the list of clouds created by GameModel
     * @param coinReserve is the value of the initial coinReserve
     */
    public Table(List<Island> islands, List<Cloud> clouds, int coinReserve) {
        this.islands = islands;
        this.clouds = clouds;
        this.motherNature = new MotherNature();
        this.coinReserve = coinReserve;
    }

    /**
     * This method calculates the position of the island/s to fuse with currentIsland
     * and then fuse them
     * @param position ("Left", "Right" or "Both") is used to know whit which island currentIsland should be fused
     * @throws GroupsOfIslandsException when there are 3 groups (islands) left (the game ends)
     */
    public void islandFusion(String position) throws GroupsOfIslandsException{
        int p;

        switch (position) {
            case "Left":
                p = motherNature.getCurrentIsland() == 0 ? islands.size() - 1 : motherNature.getCurrentIsland() - 1;
                aggregator(p);
                break;
            case "Right":
                p = motherNature.getCurrentIsland() == islands.size() - 1 ? 0 : motherNature.getCurrentIsland() + 1;
                aggregator(p);
                break;
            case "Both":
                if (islands.size() > 4) {
                    p = motherNature.getCurrentIsland() == 0 ? islands.size() - 1 : motherNature.getCurrentIsland() - 1;
                    aggregator(p);
                }
                p = motherNature.getCurrentIsland() == islands.size() - 1 ? 0 : motherNature.getCurrentIsland() + 1;
                aggregator(p);
                break;
        }

        if(islands.size()==3) throw new GroupsOfIslandsException();
    }

    public int getCoinReserve() {
        return coinReserve;
    }
    public void setCoinReserve(int coinReserve) {
        this.coinReserve = coinReserve;
    }

    public List<Island> getIslands() {
        return islands;
    }

    public List<Cloud> getClouds() {
        return clouds;
    }

    public int getMnPosition(){
        return motherNature.getCurrentIsland();
    }

    private void aggregator(int p){
        int mn_curr_position = motherNature.getCurrentIsland();
        List<Student> newStudents = new ArrayList<>(islands.get(mn_curr_position).getStudents());
        newStudents.addAll(islands.get(p).getStudents());

        Island newIsland = new Island(newStudents,
                islands.get(mn_curr_position).getNumberOfTowers()+islands.get(p).getNumberOfTowers(),
                islands.get(mn_curr_position).getColorOfTowers(),
                islands.get(mn_curr_position).getCapacity(),
                islands.get(mn_curr_position).getNumberOfNoEntries()+islands.get(p).getNumberOfNoEntries());

        if(mn_curr_position==0 && p==islands.size()-1){
            islands.add(0,newIsland);
            islands.remove(1);
            islands.remove(islands.size()-1);

        }else if(mn_curr_position<p){
            islands.add(mn_curr_position,newIsland);
            islands.remove(mn_curr_position+1); //shifts to the left
            islands.remove(mn_curr_position+1);

        }else if(mn_curr_position==islands.size()-1 && p==0){
            islands.add(mn_curr_position,newIsland);
            islands.remove(islands.size()-1);
            islands.remove(0);
            motherNature.setCurrentIsland(mn_curr_position-1);
        }
        else{
            islands.add(p,newIsland);
            islands.remove(p+1);
            islands.remove(p+1);
            motherNature.setCurrentIsland(p);
        }


    }
}

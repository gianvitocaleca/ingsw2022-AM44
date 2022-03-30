package it.polimi.ingsw.model.gameboard;

import it.polimi.ingsw.model.enums.Color;
import it.polimi.ingsw.model.exceptions.GroupsOfIslandsException;
import it.polimi.ingsw.model.exceptions.StudentsOutOfStockException;
import it.polimi.ingsw.model.studentcontainers.Cloud;
import it.polimi.ingsw.model.studentcontainers.Island;
import it.polimi.ingsw.model.students.Student;
import it.polimi.ingsw.model.students.StudentBucket;

import java.util.*;

public class Table {
    private final List<Island> islands = new ArrayList<>();
    private final List<Cloud> clouds = new ArrayList<>();
    private final MotherNature motherNature;
    private int coinReserve = 0;

    /**
     * The constructor makes a new table with provided parameters
     * @param numberOfPlayers is the numberOfPlayers that will play the game
     * @param advancedRules is the boolean to set advancedRules
     */
    public Table(int numberOfPlayers, boolean advancedRules) {

        for(int i=0;i<12;i++){
            List<Student> students = new ArrayList<>();
            try {
                students.add(StudentBucket.getInstance().generateStudent());
            }catch (StudentsOutOfStockException ignored){
                ignored.printStackTrace();
            }
            this.islands.add(new Island(students,0, Color.BLACK,130,0));
        }

        createClouds(numberOfPlayers);
        this.motherNature = new MotherNature();
        if(advancedRules){
            this.coinReserve = 20-numberOfPlayers;
        }

    }

    private void createClouds(int n){
        for(int i=0;i<n;i++){
            this.clouds.add(new Cloud(n+1));
        }
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

    public void addCoin() {
        this.coinReserve++;
    }
    public void removeCoin() {
        this.coinReserve--;
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

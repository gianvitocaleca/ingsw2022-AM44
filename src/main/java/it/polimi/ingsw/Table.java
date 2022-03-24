package it.polimi.ingsw;

import java.util.*;

public class Table {
    private List<Island> islands;
    private List<Cloud> clouds;
    private MotherNature motherNature;

    private int coin_reserve;

    public Table(List<Island> islands, List<Cloud> clouds, int coin_reserve) {
        this.islands = islands;
        this.clouds = clouds;
        this.motherNature = new MotherNature(new Random().nextInt(11));
        this.coin_reserve = coin_reserve;
    }

    public void islandFusion(String position) throws GroupsOfIslandsException{
        int p;

        if (position.equals("Left")) {
            p = motherNature.getCurrentIsland() == 0 ? islands.size()-1 : motherNature.getCurrentIsland()-1;
            aggregator(p);
        } else if (position.equals("Right")) {
            p = motherNature.getCurrentIsland() == islands.size()-1 ? 0  : motherNature.getCurrentIsland()+1;
            aggregator(p);
        }else if(position.equals("Both")){
            if(islands.size()>4){
                p = motherNature.getCurrentIsland() == 0 ? islands.size()-1 : motherNature.getCurrentIsland()-1;
                aggregator(p);
            }
            p = motherNature.getCurrentIsland() == islands.size()-1 ? 0  : motherNature.getCurrentIsland()+1;
            aggregator(p);
        }
        if(islands.size()==3) throw new GroupsOfIslandsException();
    }

    private void aggregator(int p){
        int mn_curr_position = motherNature.getCurrentIsland();
        List<Student> newStudents = new ArrayList<Student>(islands.get(mn_curr_position).getStudents());
        newStudents.addAll(islands.get(p).getStudents());

        Island newIsland = new Island(newStudents,
                islands.get(mn_curr_position).getTower()+islands.get(p).getTower(),
                islands.get(mn_curr_position).getColorOfTower(),
                islands.get(mn_curr_position).capacity,
                islands.get(mn_curr_position).getNoEntry()+islands.get(p).getNoEntry());

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

    private List<Student> addStudentsFromPosition(int pos) {
        return islands.get(pos).students;
    }

    public int getCoin_reserve() {
        return coin_reserve;
    }
    public void setCoin_reserve(int coin_reserve) {
        this.coin_reserve = coin_reserve;
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
}

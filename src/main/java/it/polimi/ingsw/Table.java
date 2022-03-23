package it.polimi.ingsw;

import java.util.*;

public class Table {
    private List<Island> islands;
    private List<Cloud> clouds;
    private MotherNature motherNature = new MotherNature(new Random().nextInt(11));
    private int coin_reserve;

    public Table(List<Island> islands, List<Cloud> clouds, MotherNature motherNature, int coin_reserve) {
        this.islands = islands;
        this.clouds = clouds;
        this.motherNature = motherNature;
        this.coin_reserve = coin_reserve;
    }

    public void islandFusion(String position){
        int mn_curr_position = motherNature.getCurrentIsland();
        int p;

        if (position.equals("Left")) {
            p = mn_curr_position == 0 ? islands.size()-1 : mn_curr_position-1;
            aggregator(p);
        } else if (position.equals("Right")) {
            p = mn_curr_position == islands.size()-1 ? 0  : mn_curr_position+1;
            aggregator(p);
        }else if(position.equals("Both")){
            p = mn_curr_position == 0 ? islands.size()-1 : mn_curr_position-1;
            aggregator(p);
            p = mn_curr_position == islands.size()-1 ? 0  : mn_curr_position+1;
            aggregator(p);
        }

    }

    private void aggregator(int p){
        int mn_curr_position = motherNature.getCurrentIsland();
        List<Student> newStudents = new ArrayList<Student>(addStudentsFromPosition(mn_curr_position));

        newStudents.addAll(addStudentsFromPosition(p));

        Island newIsland = new Island(newStudents,
                islands.get(mn_curr_position).getTower()+islands.get(p).getTower(),
                islands.get(mn_curr_position).getColorOfTower(),
                islands.get(mn_curr_position).capacity,
                islands.get(mn_curr_position).getNoEntry()+islands.get(p).getNoEntry());

        if(mn_curr_position<p){
            islands.add(mn_curr_position,newIsland);
            islands.remove(mn_curr_position+1); //shifts to the left
            islands.remove(mn_curr_position+1);
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
}

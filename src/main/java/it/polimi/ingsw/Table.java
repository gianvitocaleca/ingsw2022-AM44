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
        List<Student> newStudents = new ArrayList<Student>();
        newStudents.addAll(islands.get(mn_curr_position).students);
        if(position.equals("Left")){
            if(mn_curr_position == 0){
                newStudents.addAll(islands.get(islands.size()-1).students);
            }else if(mn_curr_position == islands.size()-1){
                newStudents.addAll(islands.get(0).students);
            }else{
                newStudents.addAll(islands.get(mn_curr_position-1).students);
            }
        }else if(position.equals("Right")){
            if(mn_curr_position == 0){
                newStudents.addAll(islands.get(mn_curr_position+1).students);
            }else if(mn_curr_position == islands.size()-1){
                newStudents.addAll(islands.get(0).students);
            }else{
                newStudents.addAll(islands.get(mn_curr_position-1).students);
            }
        }
    }
}

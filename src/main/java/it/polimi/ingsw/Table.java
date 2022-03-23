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
        List<Student> newStudents = new ArrayList<Student>(addStudentsFromPosition(mn_curr_position));

        if (position.equals("Left")) {
            newStudents.addAll(mn_curr_position == 0 ?
                    addStudentsFromPosition(islands.size() - 1) :
                    addStudentsFromPosition(mn_curr_position - 1));

        } else if (position.equals("Right")) {
            newStudents.addAll(mn_curr_position == islands.size() - 1 ?
                    addStudentsFromPosition(0) :
                    addStudentsFromPosition(mn_curr_position + 1));
        }
    }

    private List<Student> addStudentsFromPosition(int pos) {
        return islands.get(pos).students;
    }
}

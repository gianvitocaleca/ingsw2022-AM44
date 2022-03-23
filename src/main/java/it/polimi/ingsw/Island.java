package it.polimi.ingsw;

import java.util.*;

public class Island extends StudentContainer {
    private int tower;
    private Color coloroftower;
    private int noEntry;

    /**
     * This is the Constructor used for the first creation of the island and the following fusions.
     * @param noEntry is the number of no entry tiles put on the island
     */
    public Island(List<Student> students, int numberoftowers, Color coloroftower,int capacity, int noEntry){
        super(capacity);
        tower = numberoftowers;
        this.noEntry = noEntry;
        this.coloroftower=coloroftower;
    }

    public Color getColorOfTower() {
        return coloroftower;
    }

    /**
     *
     * @return is the number of tower on the island,
     * that is one on each island and more than one in case of islands' fusion
     */
    public int getTower() {
        return tower;
    }

    /**
     *
     * @param color is the new tower's color in case of conquer
     */
    public void changeTower(Color color){
        this.coloroftower=color;
        if(tower == 0){
            tower++;
        }
    }
    public void addNoEntry(){noEntry+=1;}
    public void removeNoEntry(){noEntry-=1;}
    public int getNoEntry(){return noEntry;}

}

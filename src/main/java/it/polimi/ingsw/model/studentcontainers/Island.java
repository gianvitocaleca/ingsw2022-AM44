package it.polimi.ingsw.model.studentcontainers;

import it.polimi.ingsw.model.enums.Color;
import it.polimi.ingsw.model.students.Student;

import java.util.*;

public class Island extends StudentContainer {
    private int numberOfTowers;
    private Color colorOfTowers;
    private int numberOfNoEntries;

    /**
     * This is the Constructor used for the first creation of the island and the following fusions.
     *
     * @param noEntries is the number of no entry tiles put on the island
     */
    public Island(List<Student> students, int numberOfTowers, Color colorOfTowers, int capacity, int noEntries) {
        super(capacity);
        addStudents(students);
        this.numberOfTowers = numberOfTowers;
        this.numberOfNoEntries = noEntries;
        this.colorOfTowers = colorOfTowers;
    }

    public boolean addNoEntry() {
        numberOfNoEntries += 1;
        return true;
    }

    public void removeNoEntry() {
        numberOfNoEntries -= 1;
        System.out.println("ho tolto la no entry all'isola corrente");
    }

    /**
     * @return the numberOfTowers on the island,
     * that can be one or more on each island depending on the number of fusions performed
     */
    public int getNumberOfTowers() {
        return numberOfTowers;
    }

    public Color getColorOfTowers() {
        return colorOfTowers;
    }

    /**
     * This method changes the Tower's color and in case of first conquer sets numberOfTowers at 1
     * The color is BLACK by default, but the island is not considered in possess of any player until numberOfTowers is 0
     *
     * @param color is the new Tower's color in case of conquer
     */
    public void setColorOfTowers(Color color) {
        this.colorOfTowers = color;
        if (numberOfTowers == 0) numberOfTowers++;
    }

    public int getNumberOfNoEntries() {
        return numberOfNoEntries;
    }

    @Override
    public String toString() {
        return "NoEntries: " + this.numberOfNoEntries + " Color: " + this.colorOfTowers +
                " NumberOfTowers: " + this.numberOfTowers + " " + this.getCapacity();
    }

}

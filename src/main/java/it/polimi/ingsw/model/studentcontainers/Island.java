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

    /**
     * Used to add a ticket of no entry on the island.
     * Used by the herbalist character effect.
     * @return whether the operation was successful
     */
    public boolean addNoEntry() {
        numberOfNoEntries += 1;
        return true;
    }

    /**
     * Used to remove a ticket of no entry on the island.
     * Triggered by mother nature movement.
     */
    public void removeNoEntry() {
        numberOfNoEntries -= 1;
    }

    /**
     * @return the numberOfTowers on the island,
     * that can be one or more on each island depending on the number of fusions performed
     */
    public int getNumberOfTowers() {
        return numberOfTowers;
    }

    /**
     *
     * @return is the color of the towers on the island
     */
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

    /**
     *
     * @return is the number of no entries tickets on the island
     */
    public int getNumberOfNoEntries() {
        return numberOfNoEntries;
    }

    /**
     * Used for test purposes.
     * @return is the island representation
     */
    @Override
    public String toString() {
        return "NoEntries: " + this.numberOfNoEntries + " Color: " + this.colorOfTowers +
                " NumberOfTowers: " + this.numberOfTowers + " " + this.getCapacity();
    }

}

package it.polimi.ingsw;

import java.util.*;

public class Island extends StudentContainer {
    private int towers;
    private Color color_of_tower;
    private boolean disabled;
    private Optional<Professor> professor;

    public Island(List<Student> students, int num_of_towers, Color color_of_tower) {
        super();
        this.students.addAll(students);
        this.towers = num_of_towers;
        this.color_of_tower = color_of_tower;
        this.disabled = false;
    }

    public int getNumOfTowers() {
        return towers;
    }

    public Color getColorOfTower() {
        return color_of_tower;
    }

    public void setColorOfTower(Color color_of_tower) {
        this.color_of_tower = color_of_tower;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public Optional<Professor> getProfessor() {
        return professor;
    }

    public void setProfessor(Optional<Professor> professor) {
        this.professor = professor;
    }
}

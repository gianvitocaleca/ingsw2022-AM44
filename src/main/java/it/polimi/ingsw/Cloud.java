package it.polimi.ingsw;

public class Cloud extends StudentContainer {
    private final int max_student;

    public Cloud(int max_student) {
        this.max_student = max_student;
    }

    public int getMax_student() {
        return max_student;
    }
}

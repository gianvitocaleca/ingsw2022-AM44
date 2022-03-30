package it.polimi.ingsw;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DiningRoomTest {
    private final int Max_cap = 50;
    public DiningRoom dr;

    @BeforeEach
    public void Initialize() {
        dr = new DiningRoom(Max_cap);

    }

    @Test
    void addStudents() {
    }

    @Test
    void removeStudent() {
    }
}
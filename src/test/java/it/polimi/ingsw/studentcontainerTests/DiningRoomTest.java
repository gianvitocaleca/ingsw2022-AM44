package it.polimi.ingsw.studentcontainerTests;

import it.polimi.ingsw.model.studentcontainers.DiningRoom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DiningRoomTest {
    private final int Max_cap = 50;
    public DiningRoom dr;

    /**
     * Creates a new dining room
     */

    @BeforeEach
    public void InitializeDiningRoom() {
        dr = new DiningRoom(Max_cap);
    }

}
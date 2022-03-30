package it.polimi.ingsw;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class CloudTest {
    public final int Max_cap = 130;
    public int capacity;
    public Cloud cloud;

    @BeforeEach
    public void Initialize() {
        capacity = new Random().nextInt(Max_cap);
        cloud = new Cloud(capacity);
    }

    @Test
    public void getCapacity() {
        assertEquals(capacity, cloud.getCapacity());
    }

}
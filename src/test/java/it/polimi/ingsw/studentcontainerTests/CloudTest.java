package it.polimi.ingsw.studentcontainerTests;

import it.polimi.ingsw.server.model.studentcontainers.Cloud;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class CloudTest {
    public final int Max_cap = 130;
    public int capacity;
    public Cloud cloud;

    /**
     * Creates a new cloud with random capacity within Max_cap
     */

    @BeforeEach
    public void InitializeCloud() {
        capacity = new Random().nextInt(Max_cap);
        cloud = new Cloud(capacity);
    }

    /**
     * The Island's capacity should equal the return value of getCapacity
     */
    
    @Test
    public void getCapacity() {
        assertEquals(capacity, cloud.getCapacity());
    }

}
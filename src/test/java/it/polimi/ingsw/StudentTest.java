package it.polimi.ingsw;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.nio.channels.ScatteringByteChannel;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class StudentTest {
    @Test
    @Timeout(value = 10, unit= TimeUnit.SECONDS)
    public void MaxCapacityBucket() {
        StudentBucket bucket = new StudentBucket();
        List<Student> temp = new ArrayList<Student>();
        while(true) {
            try {
                temp.add(bucket.generateStudent());
            } catch (StudentsOutOfStockException ex) {
                break;
            }
        }
        int sum = 0;
        for (Creature c : Creature.values()) {
            sum += bucket.getNumberOfCreature(c);
        }
        System.out.println(sum);
        assertEquals(sum, 130);
    }

}

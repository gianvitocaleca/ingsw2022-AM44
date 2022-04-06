package it.polimi.ingsw.studentsTests;

import it.polimi.ingsw.model.enums.Creature;
import it.polimi.ingsw.model.exceptions.StudentsOutOfStockException;
import it.polimi.ingsw.model.students.Student;
import it.polimi.ingsw.model.students.StudentBucket;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StudentBucketTest {
    private int maxBucketCapacity = 130;

    @Test
    public void MaxCapacityBucket() {
        StudentBucket.resetMap();
        StudentBucket bucket = StudentBucket.getInstance();
        List<Student> temp = new ArrayList<>();
        while (true) {
            try {
                temp.add(bucket.generateStudent());
            } catch (StudentsOutOfStockException ex) {
                break;
            }
        }
        int sum = 0;
        for (Creature c : Creature.values()) {
            sum += bucket.getNumberOfGeneratedStudentsByCreature(c);
            assertEquals(bucket.getNumberOfGeneratedStudentsByCreature(c), 26);
        }
        assertEquals(maxBucketCapacity, sum);
        assertEquals(maxBucketCapacity, temp.size());
    }
}

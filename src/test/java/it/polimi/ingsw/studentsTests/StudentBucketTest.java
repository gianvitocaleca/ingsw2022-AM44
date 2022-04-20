package it.polimi.ingsw.studentsTests;

import it.polimi.ingsw.model.enums.Creature;
import it.polimi.ingsw.model.exceptions.StudentsOutOfStockException;
import it.polimi.ingsw.model.students.Student;
import it.polimi.ingsw.model.students.StudentBucket;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StudentBucketTest {

    private int maxStudentCap = 130;

    @Test
    public void MaxCapacityBucket() {
        StudentBucket bucket = new StudentBucket();
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
        assertEquals(maxStudentCap, sum);
        assertEquals(maxStudentCap, temp.size());
    }

}

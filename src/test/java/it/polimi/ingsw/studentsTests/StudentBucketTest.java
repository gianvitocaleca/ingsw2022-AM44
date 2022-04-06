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
    @Test
    public void MaxCapacityBucket() {
        StudentBucket bucket = StudentBucket.getInstance();
        List<Student> temp = new ArrayList<>();
        while(true) {
            try {
                temp.add(bucket.generateStudent());
            } catch (StudentsOutOfStockException ex) {
                break;
            }
        }
        int sum = 0;
        for (Creature c : Creature.values()) {
            sum += bucket.getNumberOfGeneratedStudentsByCreature(c);
            assertEquals(bucket.getNumberOfGeneratedStudentsByCreature(c),26);
        }
        assertEquals(sum, 130);
        assertEquals(temp.size(),130);
    }

    @BeforeEach
    public void resetBucket() {
        StudentBucket.resetMap();
    }
}

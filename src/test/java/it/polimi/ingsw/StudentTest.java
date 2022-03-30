package it.polimi.ingsw;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.model.enums.Creature;
import it.polimi.ingsw.model.students.Student;
import it.polimi.ingsw.model.students.StudentBucket;
import it.polimi.ingsw.model.exceptions.StudentsOutOfStockException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.*;

public class StudentTest {
    @Test
    @Timeout(value = 10)
    public void MaxCapacityBucket() {
        StudentBucket bucket = new StudentBucket();
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
        System.out.println(sum);
        assertEquals(sum, 130);
        assertEquals(temp.size(),130);
    }

}

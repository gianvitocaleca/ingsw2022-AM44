package it.polimi.ingsw.studentsTests;

import it.polimi.ingsw.model.enums.Creature;
import it.polimi.ingsw.model.exceptions.StudentsOutOfStockException;
import it.polimi.ingsw.model.students.Student;
import it.polimi.ingsw.model.students.StudentBucket;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * This class tests the StudentBucket methods
 */
public class StudentBucketTest {

    private int maxStudentCap = 130;

    /**
     * This tests that when 130 students are generated, trying to generate the 131st, the bucket throws a
     * StudentsOutOfStockException
     */
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

    /**
     * This tests that the resetMap method resets correctly every map value
     */
    @Test
    public void resetMapTest() {
        StudentBucket bucket = new StudentBucket();
        List<Student> temp = new ArrayList<>();
        int numberOfGeneratedStudents = 10;
        for (int i = 0; i < numberOfGeneratedStudents; i++) {
            try {
                temp.add(bucket.generateStudent());
            } catch (StudentsOutOfStockException ignore) {

            }
        }
        assertEquals(numberOfGeneratedStudents, temp.size());
        Map<Creature, Integer> map;
        map = bucket.getMap();
        for (Creature c : temp.stream().map(Student::getCreature).toList()) {
            assertTrue(map.get(c) > 0);
        }
        bucket.resetMap();
        map = bucket.getMap();
        for (Creature c : Creature.values()) {
            assertEquals(0, map.get(c));
        }

    }

}

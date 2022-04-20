package it.polimi.ingsw.studentcontainerTests;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.model.enums.Color;
import it.polimi.ingsw.model.enums.Creature;
import it.polimi.ingsw.model.exceptions.StudentsOutOfStockException;
import it.polimi.ingsw.model.studentcontainers.Island;
import it.polimi.ingsw.model.students.Student;
import it.polimi.ingsw.model.students.StudentBucket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;


public class IslandTest {
    StudentBucket sb;
    Island island;

    /**
     * Creates a new Island with a random number of students, which are randomly generated.
     */

    @BeforeEach
    void initializeIsland() {
        List<Student> students = new ArrayList<Student>();
        sb = new StudentBucket() ;
        for (int i = 0; i < new Random().nextInt(130); i++) {
            try {
                students.add(sb.generateStudent());
            } catch (StudentsOutOfStockException ignored) {
                System.out.println("Studenti finiti");
                break;
            }
        }
        island = new Island(students, 0, Color.BLACK, 130, 0);
    }

    /**
     * Checks the number of students by creatures on the island.
     */

    @Test
    void getNumberOfStudentsByCreature() {
        for (Creature c : Creature.values()) {
            int sum = 0;
            for (Student s : island.getStudents()) {
                if (s.getCreature().equals(c)) {
                    sum++;
                }
            }
            assertEquals(sum, island.getNumberOfStudentsByCreature(c));
        }

    }
}
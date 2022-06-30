package it.polimi.ingsw.gameboardTests;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.model.enums.Color;
import it.polimi.ingsw.model.exceptions.GroupsOfIslandsException;
import it.polimi.ingsw.model.gameboard.Table;
import it.polimi.ingsw.model.studentcontainers.Island;
import it.polimi.ingsw.model.students.Student;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;


public class TableTest {

    Table table;

    @BeforeEach
    public void createTable() {
        table = new Table(2, true);
    }


    /**
     * This tests the correct initialization of Tables with both standard and advanced rules
     */
    @Test
    public void getCoinReserveCorrectlyTest() {
        assertEquals(table.getCoinReserve(), 18);
        table = new Table(3, true);
        assertEquals(table.getCoinReserve(), 17);
        table = new Table(3, false);
        assertEquals(table.getCoinReserve(), 0);
        table.addCoins(1);
        table.removeCoin();
        assertEquals(table.getCoinReserve(), 0);
    }

    /**
     * This tests that with table initialization, the number of clouds is coherent with the number of players
     */
    @Test
    public void correctNumberOfCloudsTest() {
        assertEquals(table.getClouds().size(), 2);
        table = new Table(3, true);
        assertEquals(table.getClouds().size(), 3);
    }

    /**
     * This tests that the fusion with the right positioned island is correctly made
     */
    @Test
    public void RightFusionTest() {
        Island currIsland = table.getCurrentIsland();
        Island nextIsland = table.getNextIsland();
        int originalSize = table.getIslands().size();
        try {
            table.islandFusion("Right");
        } catch (GroupsOfIslandsException ignore) {
        }


        currIsland.getStudents().addAll(nextIsland.getStudents());
        List<Student> check = currIsland.getStudents();

        for (Student s : table.getCurrentIsland().getStudents()) {
            for (Student t : check) {
                if (t.getCreature().equals(s.getCreature())) {
                    check.remove(t);
                    break;
                }
            }
        }
        assertEquals(check.size(), 0);
        assertEquals(table.getCurrentIsland().getNumberOfTowers(), currIsland.getNumberOfTowers() + nextIsland.getNumberOfTowers());
        assertEquals(table.getCurrentIsland().getNumberOfNoEntries(), currIsland.getNumberOfNoEntries() + nextIsland.getNumberOfNoEntries());
        assertEquals(table.getCurrentIsland().getColorOfTowers(), currIsland.getColorOfTowers());
        assertEquals(table.getIslands().size(), originalSize - 1);
    }

    /**
     * This tests that the fusion with the left positioned island is correctly made
     */
    @Test
    public void LeftFusionTest() {
        Island currIsland = table.getCurrentIsland();
        Island prevIsland = table.getPrevIsland();
        int originalSize = table.getIslands().size();
        try {
            table.islandFusion("Left");
        } catch (GroupsOfIslandsException ignore) {
        }


        currIsland.getStudents().addAll(prevIsland.getStudents());
        List<Student> check = currIsland.getStudents();

        for (Student s : table.getCurrentIsland().getStudents()) {
            for (Student t : check) {
                if (t.getCreature().equals(s.getCreature())) {
                    check.remove(t);
                    break;
                }
            }
        }
        assertEquals(check.size(), 0);
        assertEquals(table.getCurrentIsland().getNumberOfTowers(), currIsland.getNumberOfTowers() + prevIsland.getNumberOfTowers());
        assertEquals(table.getCurrentIsland().getNumberOfNoEntries(), currIsland.getNumberOfNoEntries() + prevIsland.getNumberOfNoEntries());
        assertEquals(table.getCurrentIsland().getColorOfTowers(), currIsland.getColorOfTowers());
        assertEquals(table.getIslands().size(), originalSize - 1);
    }

    /**
     * This tests that the fusion with both left and right positioned islands is correctly made
     */
    @Test
    public void BothFusionTest() {
        Island currIsland = table.getCurrentIsland();
        Island nextIsland = table.getNextIsland();
        Island prevIsland = table.getPrevIsland();
        int originalSize = table.getIslands().size();
        try {
            table.islandFusion("Both");
        } catch (GroupsOfIslandsException ignore) {
        }


        List<Student> check = currIsland.getStudents();
        check.addAll(prevIsland.getStudents());
        check.addAll(nextIsland.getStudents());




        assertEquals(table.getCurrentIsland().getStudents().get(0).getCreature(),
                check.get(0).getCreature());
        assertEquals(table.getCurrentIsland().getStudents().get(1).getCreature(),
                check.get(1).getCreature());
        assertEquals(table.getCurrentIsland().getStudents().get(2).getCreature()
                , check.get(2).getCreature());
        assertEquals(table.getCurrentIsland().getNumberOfTowers(), currIsland.getNumberOfTowers() + nextIsland.getNumberOfTowers() + prevIsland.getNumberOfTowers());
        assertEquals(table.getCurrentIsland().getNumberOfNoEntries(), currIsland.getNumberOfNoEntries() + nextIsland.getNumberOfNoEntries() + prevIsland.getNumberOfNoEntries());
        assertEquals(table.getCurrentIsland().getColorOfTowers(), currIsland.getColorOfTowers());
        assertEquals(table.getIslands().size(), originalSize - 2);
    }

    /**
     * This tests that when there are only 3 islands left the method islandFusion throws a GroupOfIslandsException
     */
    @Test
    public void LastFusionTest() {

        while (true) {
            try {
                table.islandFusion("Both");
            } catch (GroupsOfIslandsException e) {
                assertEquals(3, table.getIslands().size());
                break;
            }
        }
    }

    @Test
    public void setPrevIslandTest() {
        Island newPrevIsland = new Island(new ArrayList<>(), 1, Color.BLACK, 130, 0);
        table.setPrevIsland(newPrevIsland);
        assertEquals(newPrevIsland.getStudents().size(),
                table.getPrevIsland().getStudents().size());
        assertEquals(newPrevIsland.getNumberOfTowers(),
                table.getPrevIsland().getNumberOfTowers());
        assertEquals(newPrevIsland.getColorOfTowers(),
                table.getPrevIsland().getColorOfTowers());
        assertEquals(newPrevIsland.getCapacity(),
                table.getPrevIsland().getCapacity());
        assertEquals(newPrevIsland.getNumberOfNoEntries(),
                table.getPrevIsland().getNumberOfNoEntries());
    }

}
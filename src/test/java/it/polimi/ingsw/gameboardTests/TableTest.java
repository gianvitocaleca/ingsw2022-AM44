package it.polimi.ingsw.gameboardTests;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.model.exceptions.GroupsOfIslandsException;
import it.polimi.ingsw.model.gameboard.Table;
import it.polimi.ingsw.model.studentcontainers.Island;
import it.polimi.ingsw.model.students.Student;
import it.polimi.ingsw.model.students.StudentBucket;
import org.junit.jupiter.api.*;

import java.util.List;


public class TableTest {

    Table table;

    @BeforeEach
    public void createTable(){
        table = new Table(2,true);
    }


    /**
     * This tests the correct initialization of Tables with both standard and advanced rules
     */
    @Test
    public void getCoinReserveCorrectlyTest(){
        assertEquals(table.getCoinReserve(),18);
        table = new Table(3,true);
        assertEquals(table.getCoinReserve(),17);
        table = new Table(3,false);
        assertEquals(table.getCoinReserve(),0);
        table.addCoins(1);
        table.removeCoin();
        assertEquals(table.getCoinReserve(),0);
    }

    /**
     * This tests that with table initialization, the number of clouds is coherent with the number of players
     */
    @Test
    public void correctNumberOfCloudsTest(){
        assertEquals(table.getClouds().size(),2);
        table = new Table(3,true);
        assertEquals(table.getClouds().size(),3);
    }

    /**
     * This tests that the fusion with the right positioned island is correctly made
     */
    @Test
    public void RightFusionTest(){
        Island currIsland = table.getCurrentIsland();
        Island nextIsland = table.getNextIsland();
        System.out.println("Right Fusion Test:");
        int originalSize = table.getIslands().size();
        try {
            table.islandFusion("Right");
        }catch (GroupsOfIslandsException e){
            System.out.println("No more island to get fused");
        }


        for(int i=0;i<table.getIslands().size();i++){
            System.out.println("Index: "+ i + " ");
            for(Student c: table.getIslands().get(i).getStudents()){
                System.out.print(c.getCreature()+" ");
            }
            System.out.println();
            System.out.println(table.getIslands().get(i));
        }
            currIsland.getStudents().addAll(nextIsland.getStudents());
            List<Student> check = currIsland.getStudents();

            for(Student s: table.getCurrentIsland().getStudents()){
                for(Student t: check){
                    if(t.getCreature().equals(s.getCreature())){
                        check.remove(t);
                        break;
                    }
                }
            }
            assertEquals(check.size(),0);
            assertEquals(table.getCurrentIsland().getNumberOfTowers(), currIsland.getNumberOfTowers()+nextIsland.getNumberOfTowers());
            assertEquals(table.getCurrentIsland().getNumberOfNoEntries(), currIsland.getNumberOfNoEntries()+nextIsland.getNumberOfNoEntries());
            assertEquals(table.getCurrentIsland().getColorOfTowers(), currIsland.getColorOfTowers());
            assertEquals(table.getIslands().size(),originalSize-1);
    }

    /**
     * This tests that the fusion with the left positioned island is correctly made
     */
    @Test
    public void LeftFusionTest(){
        Island currIsland = table.getCurrentIsland();
        Island prevIsland = table.getPrevIsland();
        System.out.println("Left Fusion Test:");
        int originalSize = table.getIslands().size();
        try {
            table.islandFusion("Left");
        }catch (GroupsOfIslandsException e){
            System.out.println("No more island to get fused");
        }

        for(int i=0;i<table.getIslands().size();i++){
            System.out.println("Index: "+ i + " ");
            for(Student c: table.getIslands().get(i).getStudents()){
                System.out.print(c.getCreature()+" ");
            }
            System.out.println();
            System.out.println(table.getIslands().get(i));
        }

        currIsland.getStudents().addAll(prevIsland.getStudents());
        List<Student> check = currIsland.getStudents();

        for(Student s: table.getCurrentIsland().getStudents()){
            for(Student t: check){
                if(t.getCreature().equals(s.getCreature())){
                    check.remove(t);
                    break;
                }
            }
        }
        assertEquals(check.size(),0);
        assertEquals(table.getCurrentIsland().getNumberOfTowers(), currIsland.getNumberOfTowers()+prevIsland.getNumberOfTowers());
        assertEquals(table.getCurrentIsland().getNumberOfNoEntries(), currIsland.getNumberOfNoEntries()+prevIsland.getNumberOfNoEntries());
        assertEquals(table.getCurrentIsland().getColorOfTowers(), currIsland.getColorOfTowers());
        assertEquals(table.getIslands().size(),originalSize-1);
    }
    /**
     * This tests that the fusion with both left and right positioned islands is correctly made
     */
    @Test
    public void BothFusionTest(){
        Island currIsland = table.getCurrentIsland();
        Island nextIsland = table.getNextIsland();
        Island prevIsland = table.getPrevIsland();
        System.out.println("Both Fusion Test:");
        int originalSize = table.getIslands().size();
        try {
            table.islandFusion("Both");
        }catch (GroupsOfIslandsException e){
            System.out.println("No more island to get fused");
        }

        for(int i=0;i<table.getIslands().size();i++){
            System.out.println("Index: "+ i + " ");
            for(Student c: table.getIslands().get(i).getStudents()){
                System.out.print(c.getCreature()+" ");
            }
            System.out.println();
            System.out.println(table.getIslands().get(i));
        }
        List<Student> check = currIsland.getStudents();
        check.addAll(prevIsland.getStudents());
        check.addAll(nextIsland.getStudents());

        for(Student c: currIsland.getStudents()){
            System.out.print(c.getCreature()+" ");
        }


        assertEquals(table.getCurrentIsland().getStudents().get(0).getCreature(),
                check.get(0).getCreature());
        assertEquals(table.getCurrentIsland().getStudents().get(1).getCreature(),
                check.get(1).getCreature());
        assertEquals(table.getCurrentIsland().getStudents().get(2).getCreature()
                ,check.get(2).getCreature());
        assertEquals(table.getCurrentIsland().getNumberOfTowers(), currIsland.getNumberOfTowers()+nextIsland.getNumberOfTowers()+prevIsland.getNumberOfTowers());
        assertEquals(table.getCurrentIsland().getNumberOfNoEntries(), currIsland.getNumberOfNoEntries()+nextIsland.getNumberOfNoEntries()+prevIsland.getNumberOfNoEntries());
        assertEquals(table.getCurrentIsland().getColorOfTowers(), currIsland.getColorOfTowers());
        assertEquals(table.getIslands().size(),originalSize-2);
    }

    /**
     * This tests that when there are only 3 islands left the method islandFusion throws a GroupOfIslandsException
     */
    @Test
    public void LastFusionTest(){
        System.out.println("Last Fusion Test:");
        while(true){
            try{
                table.islandFusion("Both");
            }
            catch (GroupsOfIslandsException e){
                System.out.println("Last fusion made");
                assertEquals(3,table.getIslands().size());
                break;
            }
        }
    }



}
package it.polimi.ingsw.gameboardTests;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.model.exceptions.GroupsOfIslandsException;
import it.polimi.ingsw.model.gameboard.Table;
import it.polimi.ingsw.model.studentcontainers.Island;
import it.polimi.ingsw.model.students.Student;
import it.polimi.ingsw.model.students.StudentBucket;
import org.junit.jupiter.api.*;


public class TableTest {

    Table table;

    @BeforeEach
    public void createTable(){
        table = new Table(2,true);
    }

    @AfterEach
    public void resetBucket(){
        StudentBucket.resetMap();
    }

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

    @Test
    public void correctNumberOfCloudsTest(){
        assertEquals(table.getClouds().size(),2);
        table = new Table(3,true);
        assertEquals(table.getClouds().size(),3);
    }

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


        for(int i=0;i<11;i++){
            System.out.println("Index: "+ i + " ");
            for(Student c: table.getIslands().get(i).getStudents()){
                System.out.print(c.getCreature()+" ");
            }
            System.out.println();
            System.out.println(table.getIslands().get(i));
        }
            currIsland.getStudents().addAll(nextIsland.getStudents());
            assertEquals(table.getCurrentIsland().getStudents(),currIsland.getStudents());
            assertEquals(table.getCurrentIsland().getNumberOfTowers(), currIsland.getNumberOfTowers()+nextIsland.getNumberOfTowers());
            assertEquals(table.getCurrentIsland().getNumberOfNoEntries(), currIsland.getNumberOfNoEntries()+nextIsland.getNumberOfNoEntries());
            assertEquals(table.getCurrentIsland().getColorOfTowers(), currIsland.getColorOfTowers());
            assertEquals(table.getIslands().size(),originalSize-1);
    }
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

        for(int i=0;i<11;i++){
            System.out.println("Index: "+ i + " ");
            for(Student c: table.getIslands().get(i).getStudents()){
                System.out.print(c.getCreature()+" ");
            }
            System.out.println();
            System.out.println(table.getIslands().get(i));
        }

        currIsland.getStudents().addAll(prevIsland.getStudents());
        assertEquals(table.getCurrentIsland().getStudents(),currIsland.getStudents());
        assertEquals(table.getCurrentIsland().getNumberOfTowers(), currIsland.getNumberOfTowers()+prevIsland.getNumberOfTowers());
        assertEquals(table.getCurrentIsland().getNumberOfNoEntries(), currIsland.getNumberOfNoEntries()+prevIsland.getNumberOfNoEntries());
        assertEquals(table.getCurrentIsland().getColorOfTowers(), currIsland.getColorOfTowers());
        assertEquals(table.getIslands().size(),originalSize-1);
    }
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

        for(int i=0;i<10;i++){
            System.out.println("Index: "+ i + " ");
            for(Student c: table.getIslands().get(i).getStudents()){
                System.out.print(c.getCreature()+" ");
            }
            System.out.println();
            System.out.println(table.getIslands().get(i));
        }

        currIsland.getStudents().addAll(prevIsland.getStudents());
        currIsland.getStudents().addAll(nextIsland.getStudents());

        for(Student c: currIsland.getStudents()){
            System.out.print(c.getCreature()+" ");
        }


        assertEquals(table.getCurrentIsland().getStudents().get(0),currIsland.getStudents().get(0));
        assertEquals(table.getCurrentIsland().getStudents().get(1),currIsland.getStudents().get(1));
        assertEquals(table.getCurrentIsland().getStudents().get(2),currIsland.getStudents().get(2));
        assertEquals(table.getCurrentIsland().getNumberOfTowers(), currIsland.getNumberOfTowers()+nextIsland.getNumberOfTowers()+prevIsland.getNumberOfTowers());
        assertEquals(table.getCurrentIsland().getNumberOfNoEntries(), currIsland.getNumberOfNoEntries()+nextIsland.getNumberOfNoEntries()+prevIsland.getNumberOfNoEntries());
        assertEquals(table.getCurrentIsland().getColorOfTowers(), currIsland.getColorOfTowers());
        assertEquals(table.getIslands().size(),originalSize-2);
    }
    @Test
    public void LastFusionTest(){
        System.out.println("Last Fusion Test:");
        while(true){
            Island currIsland = table.getCurrentIsland();
            Island nextIsland = table.getNextIsland();
            Island prevIsland = table.getPrevIsland();
            int originalSize = table.getIslands().size();
            try{
                table.islandFusion("Both");
                currIsland.getStudents().addAll(prevIsland.getStudents());
                currIsland.getStudents().addAll(nextIsland.getStudents());

                assertEquals(table.getCurrentIsland().getStudents().get(0),currIsland.getStudents().get(0));
                assertEquals(table.getCurrentIsland().getStudents().get(1),currIsland.getStudents().get(1));
                assertEquals(table.getCurrentIsland().getStudents().get(2),currIsland.getStudents().get(2));
                assertEquals(table.getCurrentIsland().getNumberOfTowers(), currIsland.getNumberOfTowers()+nextIsland.getNumberOfTowers()+prevIsland.getNumberOfTowers());
                assertEquals(table.getCurrentIsland().getNumberOfNoEntries(), currIsland.getNumberOfNoEntries()+nextIsland.getNumberOfNoEntries()+prevIsland.getNumberOfNoEntries());
                assertEquals(table.getCurrentIsland().getColorOfTowers(), currIsland.getColorOfTowers());
                assertEquals(table.getIslands().size(),originalSize-2);
            }
            catch (GroupsOfIslandsException e){
                System.out.println("Last fusion made");
                assertEquals(3,table.getIslands().size());
                break;
            }
        }
    }



}
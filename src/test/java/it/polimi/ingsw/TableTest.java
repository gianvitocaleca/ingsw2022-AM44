package it.polimi.ingsw;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import java.util.*;


public class TableTest {

    List<Island> island;
    StudentBucket bucket;
    List<Student> newStudents;
    Table table;

    @BeforeEach
    public void InitializeTable(){
        System.out.println("Initializing Table");
        island = new ArrayList<Island>();
        bucket = new StudentBucket();
        for (int i = 0; i < 12; i++) {
            try{
                newStudents= new ArrayList<Student>();
                newStudents.add(bucket.generateStudent());
                island.add(i, new Island(newStudents,1,Color.BLACK,130,0));
            }catch (StudentsOutOfStockException e){
                System.out.println("No more students");
            }

        }
        for(int i=0;i<12;i++){
            System.out.println("Index: "+ i + " ");
            for(Student c: island.get(i).students){
                System.out.println(c.getCreature());
            }
            System.out.println(island.get(i));
        }
        table = new Table(island,new ArrayList<Cloud>(),0);
        System.out.println("Table Initialized");
    }

    @RepeatedTest(1000)
    public void RightFusionTest(){
        Island currIsland = table.getIslands().get(table.getMnPosition());
        Island nextIsland = table.getMnPosition()==table.getIslands().size() ? table.getIslands().get(0) : table.getIslands().get(table.getMnPosition()+1);
        System.out.println("Right Fusion Test:");
        int originalSize = island.size();
        System.out.println(table.getMnPosition() + " is the Mn_position");
        try {
            table.islandFusion("Right");
        }catch (GroupsOfIslandsException e){
            System.out.println("No more island to get fused");
        }


        for(int i=0;i<11;i++){
            System.out.println("Index: "+ i + " ");
            for(Student c: island.get(i).students){
                System.out.print(c.getCreature()+" ");
            }
            System.out.println();
            System.out.println(island.get(i));
        }
            currIsland.getStudents().addAll(nextIsland.getStudents());
            assertEquals(island.get(table.getMnPosition()).getStudents(),currIsland.getStudents());
            assertEquals(island.get(table.getMnPosition()).getTower(), currIsland.getTower()+nextIsland.getTower());
            assertEquals(island.get(table.getMnPosition()).getNoEntry(), currIsland.getNoEntry()+nextIsland.getNoEntry());
            assertEquals(island.get(table.getMnPosition()).getColorOfTower(), currIsland.getColorOfTower());
            assertEquals(island.size(),originalSize-1);
    }

    @RepeatedTest(1000)
    public void LeftFusionTest(){
        Island currIsland = table.getIslands().get(table.getMnPosition());
        Island nextIsland = table.getMnPosition()== 0 ? table.getIslands().get(table.getIslands().size()-1) : table.getIslands().get(table.getMnPosition()-1);
        System.out.println("Left Fusion Test:");
        int originalSize = island.size();
        System.out.println(table.getMnPosition() + "is the Mn_position");
        try {
            table.islandFusion("Left");
        }catch (GroupsOfIslandsException e){
            System.out.println("No more island to get fused");
        }

        for(int i=0;i<11;i++){
            System.out.println("Index: "+ i + " ");
            for(Student c: island.get(i).students){
                System.out.print(c.getCreature()+" ");
            }
            System.out.println();
            System.out.println(island.get(i));
        }

        currIsland.getStudents().addAll(nextIsland.getStudents());
        assertEquals(island.get(table.getMnPosition()).getStudents(),currIsland.getStudents());
        assertEquals(island.get(table.getMnPosition()).getTower(), currIsland.getTower()+nextIsland.getTower());
        assertEquals(island.get(table.getMnPosition()).getNoEntry(), currIsland.getNoEntry()+nextIsland.getNoEntry());
        assertEquals(island.get(table.getMnPosition()).getColorOfTower(), currIsland.getColorOfTower());
        assertEquals(island.size(),originalSize-1);
    }
    @RepeatedTest(1000)
    public void BothFusionTest(){
        Island currIsland = table.getIslands().get(table.getMnPosition());
        Island nextIsland = table.getMnPosition()==table.getIslands().size() ? table.getIslands().get(0) : table.getIslands().get(table.getMnPosition()+1);
        Island prevIsland = table.getMnPosition()== 0 ? table.getIslands().get(table.getIslands().size()-1) : table.getIslands().get(table.getMnPosition()-1);
        System.out.println("Both Fusion Test:");
        int originalSize = island.size();
        System.out.println(table.getMnPosition() + "is the Mn_position");
        try {
            table.islandFusion("Both");
        }catch (GroupsOfIslandsException e){
            System.out.println("No more island to get fused");
        }

        for(int i=0;i<10;i++){
            System.out.println("Index: "+ i + " ");
            for(Student c: island.get(i).students){
                System.out.print(c.getCreature()+" ");
            }
            System.out.println();
            System.out.println(island.get(i));
        }

        currIsland.getStudents().addAll(prevIsland.getStudents());
        currIsland.getStudents().addAll(nextIsland.getStudents());

        for(Student c: currIsland.getStudents()){
            System.out.print(c.getCreature()+" ");
        }


        assertEquals(island.get(table.getMnPosition()).getStudents().get(0),currIsland.getStudents().get(0));
        assertEquals(island.get(table.getMnPosition()).getStudents().get(1),currIsland.getStudents().get(1));
        assertEquals(island.get(table.getMnPosition()).getStudents().get(2),currIsland.getStudents().get(2));
        assertEquals(island.get(table.getMnPosition()).getTower(), currIsland.getTower()+nextIsland.getTower()+prevIsland.getTower());
        assertEquals(island.get(table.getMnPosition()).getNoEntry(), currIsland.getNoEntry()+nextIsland.getNoEntry()+prevIsland.getNoEntry());
        assertEquals(island.get(table.getMnPosition()).getColorOfTower(), currIsland.getColorOfTower());
        assertEquals(island.size(),originalSize-2);
    }

    @RepeatedTest(1000)
    public void LastFusionTest(){
        System.out.println("Last Fusion Test:");
        while(true){
            try{
                table.islandFusion("Both");
            }
            catch (GroupsOfIslandsException e){
                System.out.println("Last fusion");
                assertEquals(3,island.size());
                break;
            }
        }
    }


}
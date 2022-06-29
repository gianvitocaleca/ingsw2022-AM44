package it.polimi.ingsw.model.gameboard;

import it.polimi.ingsw.model.exceptions.GroupsOfIslandsException;
import it.polimi.ingsw.model.exceptions.StudentsOutOfStockException;
import it.polimi.ingsw.model.studentcontainers.Cloud;
import it.polimi.ingsw.model.studentcontainers.Island;
import it.polimi.ingsw.model.enums.Color;
import it.polimi.ingsw.model.students.Student;
import it.polimi.ingsw.model.students.StudentBucket;

import java.util.*;

public class Table {
    public static final int TOTAL_COINS_ADVANCED_RULES = 20;
    public static final int MAX_GENERATED_STUDENTS = 130;
    public static final int MIN_NUMBER_OF_ISLANDS = 3;
    final int NUMBER_OF_ISLANDS = 12;
    private List<Island> islands = new ArrayList<>();
    private List<Cloud> clouds = new ArrayList<>();
    private MotherNature motherNature;
    private int deactivators;
    private StudentBucket bucket;
    private int coinReserve = 0;

    public StudentBucket getBucket() {
        StudentBucket temp = new StudentBucket();
        temp.setMap(bucket.getMap());
        return temp;
    }

    public void setBucket(StudentBucket bucket) {
        this.bucket = bucket;
    }

    /**
     * The constructor makes a new table with provided parameters
     *
     * @param numberOfPlayers is the numberOfPlayers that will play the game
     * @param advancedRules   is the boolean to set advancedRules
     */
    public Table(int numberOfPlayers, boolean advancedRules) {
        deactivators = 0;
        bucket = new StudentBucket();

        for (int i = 0; i < NUMBER_OF_ISLANDS; i++) {
            List<Student> students = new ArrayList<>();
            try {
                students.add(bucket.generateStudent());
            } catch (StudentsOutOfStockException ignore) {
            }
            this.islands.add(new Island(students, 0, Color.BLACK, MAX_GENERATED_STUDENTS, 0));
        }

        createClouds(numberOfPlayers);
        this.motherNature = new MotherNature();
        if (advancedRules) {
            this.coinReserve = TOTAL_COINS_ADVANCED_RULES - numberOfPlayers;
        }

    }

    public boolean fillClouds() {
        List<Student> newStudentsOnCloud;
        List<Cloud> newClouds = getClouds();

        for (Cloud c : newClouds) {
            if (c.getStudents().size() == 0) {
                newStudentsOnCloud = new ArrayList<>();
                for (int i = 0; i < c.getCapacity(); i++) {
                    try {
                        newStudentsOnCloud.add(bucket.generateStudent());
                    } catch (StudentsOutOfStockException ex) {
                        return false;
                    }
                }
                c.addStudents(newStudentsOnCloud);
            }
        }
        setClouds(newClouds);
        return true;
    }

    public void moveMotherNature(int jumps) {
        int mnFuturePos = (motherNature.getCurrentIsland() + jumps) % (islands.size());
        setMotherNaturePosition(mnFuturePos);
    }

    public boolean checkNeighborIsland() {
        boolean left = false, right = false;
        Island currentIsland = getCurrentIsland(); //11
        Island nextIsland = getNextIsland(); //0
        Island prevIsland = getPrevIsland(); //10

        if (prevIsland.getNumberOfTowers() > 0 && prevIsland.getColorOfTowers().equals(currentIsland.getColorOfTowers())) {
            left = true;
        }
        if (nextIsland.getNumberOfTowers() > 0 && nextIsland.getColorOfTowers().equals(currentIsland.getColorOfTowers())) {
            right = true;
        }

        if (right && left) {
            try {
                islandFusion("Both");
            } catch (GroupsOfIslandsException e) {
                return false;
            }

        } else if (right) {
            try {
                islandFusion("Right");
            } catch (GroupsOfIslandsException e) {
                return false;
            }
        } else if (left) {
            try {
                islandFusion("Left");
            } catch (GroupsOfIslandsException e) {
                return false;
            }
        }
        return true;
    }

    public boolean setDeactivators(int deactivators) {
        this.deactivators = deactivators;
        return true;
    }

    public int getDeactivators() {
        return this.deactivators;
    }


    private void createClouds(int n) {
        for (int i = 0; i < n; i++) {
            this.clouds.add(new Cloud(n + 1));
        }
    }

    /**
     * This method calculates the position of the island/s to fuse with currentIsland
     * and then fuse them
     *
     * @param position ("Left", "Right" or "Both") is used to know whit which island currentIsland should be fused
     * @throws GroupsOfIslandsException when there are 3 groups (islands) left (the game ends)
     */
    public void islandFusion(String position) throws GroupsOfIslandsException {

        switch (position) {
            case "Left":
                aggregator(getPrevIslandPosition());
                break;
            case "Right":
                aggregator(getNextIslandPosition());
                break;
            case "Both":
                if (islands.size() > MIN_NUMBER_OF_ISLANDS + 1) {
                    aggregator(getPrevIslandPosition());
                }
                aggregator(getNextIslandPosition());
                break;
        }

        if (islands.size() == MIN_NUMBER_OF_ISLANDS) throw new GroupsOfIslandsException();
    }

    public int getCoinReserve() {
        return coinReserve;
    }

    public void addCoins(int coins) {
        this.coinReserve += coins;
    }

    public void removeCoin() {
        this.coinReserve--;
    }

    public List<Island> getIslands() {
        List<Island> temp = new ArrayList<>();
        for (Island i : islands) {
            temp.add(new Island(i.getStudents(), i.getNumberOfTowers(), i.getColorOfTowers(), i.getCapacity(), i.getNumberOfNoEntries()));
        }
        return temp;
    }


    public Island getCurrentIsland() {
        Island i = islands.get(getMnPosition());
        Island temp = new Island(i.getStudents(), i.getNumberOfTowers(), i.getColorOfTowers(), i.getCapacity(), i.getNumberOfNoEntries());
        return temp;
    }

    public void setCurrentIsland(Island island) {
        int currPos = getMnPosition();
        islands.remove(currPos);
        islands.add(currPos, island);
    }

    public Island getNextIsland() {
        Island i = islands.get(getNextIslandPosition());
        Island temp = new Island(i.getStudents(), i.getNumberOfTowers(), i.getColorOfTowers(), i.getCapacity(), i.getNumberOfNoEntries());
        return temp;
    }

    private int getPrevIslandPosition() {
        return motherNature.getCurrentIsland() == 0 ? islands.size() - 1 : motherNature.getCurrentIsland() - 1;
    }

    private int getNextIslandPosition() {
        return motherNature.getCurrentIsland() == islands.size() - 1 ? 0 : motherNature.getCurrentIsland() + 1;
    }

    public MotherNature getMotherNature() {
        MotherNature temp = new MotherNature();
        temp.setCurrentIsland(motherNature.getCurrentIsland());
        return temp;
    }

    public void setNextIsland(Island island) {
        int nextPos = getNextIslandPosition();
        islands.remove(nextPos);
        islands.add(nextPos, island);

    }

    public int getMnPosition() {
        return motherNature.getCurrentIsland();
    }

    private void aggregator(int p) {
        int mnCurrPosition = motherNature.getCurrentIsland(); //11
        List<Student> newStudents = new ArrayList<>(getCurrentIsland().getStudents());
        newStudents.addAll(islands.get(p).getStudents());

        Island newIsland = new Island(newStudents,
                getCurrentIsland().getNumberOfTowers() + islands.get(p).getNumberOfTowers(),
                getCurrentIsland().getColorOfTowers(),
                getCurrentIsland().getCapacity(),
                getCurrentIsland().getNumberOfNoEntries() + islands.get(p).getNumberOfNoEntries());

        if (mnCurrPosition == 0 && p == islands.size() - 1) {
            islands.add(0, newIsland);
            islands.remove(1);
            islands.remove(islands.size() - 1);

        } else if (mnCurrPosition < p) {
            islands.add(mnCurrPosition, newIsland);
            islands.remove(mnCurrPosition + 1); //shifts to the left
            islands.remove(mnCurrPosition + 1);

        } else if (mnCurrPosition == islands.size() - 1 && p == 0) {
            islands.add(mnCurrPosition, newIsland);
            islands.remove(islands.size() - 1);
            islands.remove(0);
            motherNature.setCurrentIsland(mnCurrPosition - 1);
        } else {
            islands.add(p, newIsland);
            islands.remove(p + 1);
            islands.remove(p + 1);
            motherNature.setCurrentIsland(p);
        }
    }

    public Island getPrevIsland() {
        Island i = islands.get(getPrevIslandPosition());
        Island temp = new Island(i.getStudents(), i.getNumberOfTowers(), i.getColorOfTowers(), i.getCapacity(), i.getNumberOfNoEntries());
        return temp;
    }

    public void setPrevIsland(Island island) {
        int prevPos = getPrevIslandPosition();
        islands.remove(prevPos);
        islands.add(prevPos, island);
    }

    public List<Cloud> getClouds() {
        List<Cloud> temp = new ArrayList<>();
        for (Cloud c : clouds) {
            Cloud tC = new Cloud(c.getCapacity());
            tC.addStudents(c.getStudents());
            temp.add(tC);
        }
        return temp;
    }


    public void setIslands(List<Island> islands) {
        this.islands = islands;
    }

    public void setClouds(List<Cloud> clouds) {
        this.clouds = clouds;
    }

    public void setMotherNature(MotherNature motherNature) {
        this.motherNature = motherNature;
    }

    public void setCoinReserve(int coinReserve) {
        this.coinReserve = coinReserve;
    }


    public boolean setMotherNaturePosition(int index) {
        motherNature.setCurrentIsland(index);
        return true;
    }

    public void setIndexIsland(int index, Island island) {
        islands.remove(index);
        islands.add(index, island);
    }
}
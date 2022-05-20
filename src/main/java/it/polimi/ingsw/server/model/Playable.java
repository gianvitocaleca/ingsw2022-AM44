package it.polimi.ingsw.server.model;

import it.polimi.ingsw.model.exceptions.GameEndedException;
import it.polimi.ingsw.server.model.enums.Creature;
import it.polimi.ingsw.server.model.evaluators.InfluenceEvaluator;
import it.polimi.ingsw.server.model.studentcontainers.StudentContainer;
import it.polimi.ingsw.server.model.students.StudentBucket;

import java.util.List;

public interface Playable {
    void addNoEntry(int indexOfIsland);

    void evaluateInfluence() throws GameEndedException;

    void setPostmanMovements(int numberOfSteps);

    void thiefEffect(Creature creature);

    boolean moveStudents(StudentContainer source, StudentContainer destination, List<Creature> creature);

    boolean princessEffect(List<Creature> sourceCreatures);

    boolean minstrelEffect(List<Creature> entranceCreatures, List<Creature> diningRoomCreatures);

    boolean jokerEffect(List<Creature> sourceCreature, List<Creature> destinationCreature);

    void setInfluenceEvaluator(InfluenceEvaluator evaluator);

    void setFarmer();

    boolean setHeraldIsland(int indexIsland) throws GameEndedException;

    boolean checkEndGame();

    public int getDeactivators();

    public boolean setDeactivators(int deactivators);
    
    StudentBucket getBucket();

    void setBucket(StudentBucket bucket);

    boolean monkEffect(List<Creature> sourceCreatures, int islandIndex);
}

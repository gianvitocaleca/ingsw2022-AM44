package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.Creature;
import it.polimi.ingsw.model.enums.Name;
import it.polimi.ingsw.model.evaluators.InfluenceEvaluator;
import it.polimi.ingsw.model.studentcontainers.StudentContainer;
import it.polimi.ingsw.model.students.StudentBucket;

import java.util.List;

public interface Playable {
    void addNoEntry(int indexOfIsland);

    void evaluateInfluence();

    void setPostmanMovements(int numberOfSteps);

    void thiefEffect(Creature creature);

    boolean moveStudents(StudentContainer source, StudentContainer destination, List<Creature> creature);

    void princessEffect(StudentContainer source, List<Creature> sourceCreatures);

    boolean minstrelEffect(List<Creature> entranceCreatures, List<Creature> diningRoomCreatures);

    boolean jokerEffect(List<Creature> sourceCreature, List<Creature> destinationCreature);

    void setInfluenceEvaluator(InfluenceEvaluator evaluator);

    void setFarmer();

    boolean setHeraldIsland(int indexIsland);

    boolean checkEndGame();

    public int getDeactivators();

    public boolean setDeactivators(int deactivators);


    StudentContainer getStudentContainer(Name name);

    StudentBucket getBucket();

    void setBucket(StudentBucket bucket);

    boolean setStudentContainer(StudentContainer container, Name name);
}

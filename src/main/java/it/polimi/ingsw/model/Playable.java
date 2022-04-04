package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.Creature;
import it.polimi.ingsw.model.studentcontainers.StudentContainer;

public interface Playable {
    void addNoEntry(int indexOfIsland);
    void evaluateInfluence();
    void setPostmanMovements( int numberOfSteps);
    void thiefEffect(Creature creature);
    void moveStudents(StudentContainer source, StudentContainer destination, Creature creature);
    void setInfluenceEvaluator(InfluenceEvaluator evaluator);
    void setFarmer();
    void setHeraldIsland(int indexIsland);
}

package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.Creature;
import it.polimi.ingsw.model.studentcontainers.StudentContainer;

public interface Playable {
    public void addNoEntry(int indexOfIsland);
    public void evaluateInfluence();
    public void setPostmanMovements( int numberOfSteps);
    public void thiefEffect(Creature creature);
    public void moveStudents(StudentContainer source, StudentContainer destination, Creature creature);
    public void setInfluenceCharacter( int typeOfInfluenceCharacter);

}

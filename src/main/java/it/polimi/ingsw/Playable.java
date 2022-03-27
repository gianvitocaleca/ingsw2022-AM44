package it.polimi.ingsw;

public interface Playable {
    public void addNoEntry(int indexOfIsland);
    public void evaluateInfluence();
    public void setPostmanMovements( int numberOfSteps);
    public void thiefEffect(Creature creature);
    public void moveStudents(StudentContainer source, StudentContainer destination, Creature creature);
    public void setInfluenceCharacter( int typeOfInfluenceCharacter);

}

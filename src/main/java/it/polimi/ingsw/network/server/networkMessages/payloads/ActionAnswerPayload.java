package it.polimi.ingsw.network.server.networkMessages.payloads;

import it.polimi.ingsw.model.enums.Creature;

public class ActionAnswerPayload implements Payload {

    private boolean moveStudents;
    private boolean moveMotherNature;
    private boolean selectCloud;
    private boolean playCharacter;

    private boolean isDestinationDiningRoom;
    private Creature studentCreatureToMove;
    private int clientInt;

    /**
     * Contains the player selection for the action phase
     * @param moveStudents whether the player wants to move the students
     * @param moveMotherNature whether the player wants to move mother nature
     * @param selectCloud whether the player wants to select a cloud
     * @param playCharacter whether the player wants to play a character
     * @param isDestinationDiningRoom whether the student container is the dining room
     * @param studentCreatureToMove the creature the player selected
     * @param clientInt is the index the player selected
     */
    public ActionAnswerPayload(boolean moveStudents, boolean moveMotherNature, boolean selectCloud,
                               boolean playCharacter, boolean isDestinationDiningRoom, Creature studentCreatureToMove, int clientInt) {
        this.moveStudents = moveStudents;
        this.moveMotherNature = moveMotherNature;
        this.selectCloud = selectCloud;
        this.playCharacter = playCharacter;
        this.isDestinationDiningRoom = isDestinationDiningRoom;
        this.studentCreatureToMove = studentCreatureToMove;
        this.clientInt = clientInt;
    }

    /**
     *
     * @return whether the player wants to move the students
     */
    public boolean isMoveStudents() {
        return moveStudents;
    }

    /**
     *
     * @return whether the player wants to move mother nature
     */
    public boolean isMoveMotherNature() {
        return moveMotherNature;
    }

    /**
     *
     * @return whether the player wants to select a cloud
     */
    public boolean isSelectCloud() {
        return selectCloud;
    }

    /**
     *
     * @return whether the player wants to play a character
     */
    public boolean isPlayCharacter() {
        return playCharacter;
    }

    /**
     *
     * @return whether the student container is the dining room
     */
    public boolean isDestinationDiningRoom() {
        return isDestinationDiningRoom;
    }

    /**
     *
     * @return the creature the player selected
     */
    public Creature getStudentCreatureToMove() {
        return studentCreatureToMove;
    }

    /**
     *
     * @return is the index the player selected
     */
    public int getClientInt() {
        return clientInt;
    }
}

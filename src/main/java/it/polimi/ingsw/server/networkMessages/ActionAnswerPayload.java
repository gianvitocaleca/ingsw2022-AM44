package it.polimi.ingsw.server.networkMessages;

import it.polimi.ingsw.server.model.enums.Creature;

public class ActionAnswerPayload {

    private boolean moveStudents;
    private boolean moveMotherNature;
    private boolean selectCloud;
    private boolean playCharacter;

    private boolean isDestinationDiningRoom;
    private Creature studentCreatureToMove;
    private int clientInt;

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

    public boolean isMoveStudents() {
        return moveStudents;
    }

    public boolean isMoveMotherNature() {
        return moveMotherNature;
    }

    public boolean isSelectCloud() {
        return selectCloud;
    }

    public boolean isPlayCharacter() {
        return playCharacter;
    }

    public boolean isDestinationDiningRoom() {
        return isDestinationDiningRoom;
    }

    public Creature getStudentCreatureToMove() {
        return studentCreatureToMove;
    }

    public int getClientInt() {
        return clientInt;
    }
}
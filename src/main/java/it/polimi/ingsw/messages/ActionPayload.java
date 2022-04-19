package it.polimi.ingsw.messages;

public class ActionPayload implements Payload {

    private boolean moveStudents;
    private boolean moveMotherNature;
    private boolean selectCloud;
    private boolean playCharacter;

    public ActionPayload(boolean moveStudents, boolean moveMotherNature, boolean selectCloud, boolean playCharacter) {
        this.moveStudents = moveStudents;
        this.moveMotherNature = moveMotherNature;
        this.selectCloud = selectCloud;
        this.playCharacter = playCharacter;
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
}

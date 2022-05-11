package it.polimi.ingsw.server.networkMessages;

public class ActionPayload implements Payload {

    private boolean moveStudents;
    private boolean moveMotherNature;
    private boolean selectCloud;
    private boolean playCharacter;

    private String currentPlayer;

    public ActionPayload(boolean moveStudents, boolean moveMotherNature, boolean selectCloud, boolean playCharacter, String currentPlayer) {
        this.moveStudents = moveStudents;
        this.moveMotherNature = moveMotherNature;
        this.selectCloud = selectCloud;
        this.playCharacter = playCharacter;
        this.currentPlayer = currentPlayer;
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

    public String getCurrentPlayer() {
        return currentPlayer;
    }
}

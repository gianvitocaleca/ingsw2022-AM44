package it.polimi.ingsw.network.server.networkMessages.payloads;

public class ActionPayload implements Payload {

    private boolean moveStudents;
    private boolean moveMotherNature;
    private boolean selectCloud;
    private boolean playCharacter;

    private String currentPlayer;

    /**
     * Contains the info for the action phase allowed actions
     * @param moveStudents whether the player can move the students
     * @param moveMotherNature whether the player can move mother nature
     * @param selectCloud whether the player can select a cloud
     * @param playCharacter whether the player can play a character
     * @param currentPlayer the name of the current player
     */
    public ActionPayload(boolean moveStudents, boolean moveMotherNature, boolean selectCloud, boolean playCharacter, String currentPlayer) {
        this.moveStudents = moveStudents;
        this.moveMotherNature = moveMotherNature;
        this.selectCloud = selectCloud;
        this.playCharacter = playCharacter;
        this.currentPlayer = currentPlayer;
    }

    /**
     *
     * @return whether the player can move the students
     */
    public boolean isMoveStudents() {
        return moveStudents;
    }

    /**
     *
     * @return whether the player can move mother nature
     */
    public boolean isMoveMotherNature() {
        return moveMotherNature;
    }

    /**
     *
     * @return whether the player can select a cloud
     */
    public boolean isSelectCloud() {
        return selectCloud;
    }

    /**
     *
     * @return whether the player can play a character
     */
    public boolean isPlayCharacter() {
        return playCharacter;
    }

    /**
     *
     * @return is the name of the current player
     */
    public String getCurrentPlayer() {
        return currentPlayer;
    }
}

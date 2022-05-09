package it.polimi.ingsw.server.model.exceptions;

public class AssistantAlreadyPlayedException extends Throwable{
    public AssistantAlreadyPlayedException() {
        super();
    }

    public AssistantAlreadyPlayedException(String message) {
        super(message);
    }
}

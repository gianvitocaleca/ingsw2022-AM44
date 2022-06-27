package it.polimi.ingsw.controller.enums;

import it.polimi.ingsw.network.server.networkMessages.Headers;

public enum GamePhases {
    CREATION_NUMBER_OF_PLAYERS(Headers.creationRequirementMessage_NumberOfPlayers),
    CREATION_RULES(Headers.creationRequirementMessage_TypeOfRules),
    LOGIN_USERNAME(Headers.loginMessage_Username),
    LOGIN_COLOR(Headers.loginMessage_Color),
    LOGIN_WIZARD(Headers.loginMessage_Wizard),
    PLANNING(Headers.planning),
    ACTION_STUDENTSMOVEMENT(Headers.action),
    ACTION_MOVEMOTHERNATURE(Headers.action),
    ACTION_CLOUDCHOICE(Headers.action),
    ACTION_PLAYED_CHARACTER(Headers.action);

    Headers header;

    GamePhases(Headers header) {
        this.header = header;
    }

    public Headers getHeader() {
        return header;
    }
}

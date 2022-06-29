package it.polimi.ingsw.network.server.networkMessages;

/**
 * Used to describe the type of payloads that are exchanged on the network
 */
public enum Headers {

    creationRequirementMessage,
    loginMessage_Username,
    loginMessage_Color,
    loginMessage_Wizard,
    ping,
    winnerPlayer,
    LOGIN,
    planning,
    errorMessage,
    action,
    characterPlayed,
    showModelMessage,
    creationRequirementMessage_NumberOfPlayers,
    creationRequirementMessage_TypeOfRules,
    closeConnection,
    reconnection
}

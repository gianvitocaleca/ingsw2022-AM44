package it.polimi.ingsw.client.GUI;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import static it.polimi.ingsw.Commands.*;

public class GuiAssets {

    public static final String selectCreatureCode = selectCreatureText + commandSeparator;
    public static String gameTitle = "Eryantis";
    public static Color backgroundColor = Color.rgb(123, 185, 230);
    public static Font titleFont = Font.font("Papyrus", FontWeight.LIGHT, 160);
    public static String joinButton = "Join";
    public static String twoPlayersButton = "Two Players";
    public static String threePlayersButton = "Three Players";
    public static String basicRulesButton = "Basic Rules";
    public static String advancedRulesButton = "Advanced Rules";
    public static String provideUsernameText = "Provide your username";
    public static final String currentPhasePrefix = "The current game phase is: ";
    public static final String moveStudentsText = "Move the students";
    public static final String moveMotherNatureText = "Move Mother Nature";
    public static final String selectCloudText = "Select a cloud";
    public static final String chooseCharacterText = " or Select a Character";
    public static final String selectAssistantText = "Select an Assistant";
    public static final String selectDestinationIslandCode = commandSeparator + selectIslandText + commandSeparator;
    public static int smallSpacing = 5;
    public static int mediumSpacing = 10;
    public static int motherNatureHeight = 28;
    public static int motherNatureWidth = 20;
    public static int islandHeight = 130;
    public static int islandWidth = 134;
    public static int cloudHeight = 140;
    public static int cloudWidth = 140;
    public static int assistantHeight = 146;
    public static int assistantWidth = 100;
    public static int characterHeight = 200;
    public static int characterWidth = 133;
    public static int creatureHeight = 100;
    public static int creatureWidth = 100;
    public static int coinHeight = 50;
    public static int coinWidth = 50;
    public static int wizardHeight = 33;
    public static int wizardWidth = 33;
    public static int playerRadius = 25;
    public static int playerContentHeight = 20;
    public static int playerContentWidth = 20;
    public static int tableTowerWidth = 15;
    public static int tableTowerHeight = tableTowerWidth * 2;
    public static int centerX = 100;
    public static int centerY = 100;
    public static int islandRadius = 300;
    public static int cloudRadius = 100;
    public static int islandContentCenter = 15;
    public static int islandContentRadius = 55;
    public static int islandContentComponents = 7;
    public static int cloudContentRadius = 40;
    public static int cloudContentComponents = 3;
    public static int noErrorCode = 0;
}

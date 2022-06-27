package it.polimi.ingsw.client.GUI;

import it.polimi.ingsw.client.ClientState;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.util.Optional;

import static it.polimi.ingsw.Commands.commandSeparator;
import static it.polimi.ingsw.Commands.selectCreatureText;
import static it.polimi.ingsw.client.GUI.GuiAssets.gameTitle;
import static it.polimi.ingsw.client.GUI.GuiAssets.noErrorCode;

public class GuiAlerts {

    /**
     * Closes the stage, sends the quit message and stops the execution
     */
    public static void quitStage(Stage stage) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to quit?",
                ButtonType.YES,
                ButtonType.NO);
        a.setTitle(gameTitle);
        a.setHeaderText("The game is still in progress...");
        ImageView logo = new ImageView(new Image("logo.png"));
        logo.setFitWidth(100);
        logo.setFitHeight(141);
        a.setGraphic(logo);
        Optional<ButtonType> confirm = a.showAndWait();
        if (confirm.isPresent() && confirm.get() == ButtonType.YES) {
            //send quit message
            //close socket thread
            stage.close();
            System.exit(noErrorCode);
        }
    }

    /**
     * Alert to show errors to the player
     *
     * @param string
     */
    public static void errorAlert(String string) {
        Alert a = new Alert(Alert.AlertType.ERROR,
                string,
                ButtonType.OK);
        a.setTitle(gameTitle);
        a.setHeaderText("Error!");
        a.showAndWait();
    }

    /**
     * Alert to show the winner to the player
     *
     * @param string
     */
    public static void winnerAlert(String string) {
        Alert a = new Alert(Alert.AlertType.INFORMATION,
                string,
                ButtonType.OK);
        a.setTitle(gameTitle);
        a.setHeaderText("Winner");
        a.showAndWait();
    }

    /**
     * Creates the alert for the characters that need to select creatures and destination
     */
    public static void characterNeedsSourceCreaturesAndDestination(ClientState clientState) {
        String string = "Select a creature and then an island";
        //guiPhases = GUIPhases.SELECT_CREATURE_FOR_CHARACTER;
        Alert a = new Alert(Alert.AlertType.INFORMATION,
                string,
                ButtonType.OK);
        a.setTitle(gameTitle);
        a.setHeaderText("Use Effect of : " + clientState.getCurrentPlayedCharacter());
        a.showAndWait();
    }

    /**
     * Shows the alert to the player to inform the character behaviour
     */
    public static void characterNeedsIslandIndex(ClientState clientState) {
        String string = "Select an island";
        //guiPhases = GUIPhases.SELECT_ISLAND;
        Alert a = new Alert(Alert.AlertType.INFORMATION,
                string,
                ButtonType.OK);
        a.setTitle(gameTitle);
        a.setHeaderText("Use Effect of : " + clientState.getCurrentPlayedCharacter());
        a.showAndWait();
    }

    /**
     * Shows the alert to the player to inform the character behaviour
     */
    public static void characterNeedsSourceCreature(ClientState clientState) {
        String string = "Select a source creature";
        //guiPhases = GUIPhases.SELECT_SOURCE_CREATURE;
        Alert a = new Alert(Alert.AlertType.INFORMATION,
                string,
                ButtonType.OK);
        a.setTitle(gameTitle);
        a.setHeaderText("Use Effect of : " + clientState.getCurrentPlayedCharacter());
        a.showAndWait();
    }

    /**
     * Shows the alert to the player to inform the character behaviour.
     * Creates the swap button.
     */
    public static void characterNeedsSwapCreatures(ClientState clientState) {
        String string = "Select at most " + clientState.getCurrentPlayedCharacter().getMaxMoves()
                + " creatures from the source";
        //guiPhases = GUIPhases.SELECT_SOURCE_CREATURE_TO_SWAP;
        Alert a = new Alert(Alert.AlertType.INFORMATION,
                string,
                ButtonType.OK);
        //createdCommand += selectCreatureText + commandSeparator;
        a.setHeaderText("Use the effect of : " + clientState.getCurrentPlayedCharacter());
        a.setTitle(gameTitle);
        a.showAndWait();
        //createSwapButton(true);
    }

    /**
     * Creates the second alert for the swap character
     */
    public static void createAlertForSwapCommand(ClientState clientState) {
        String string = "Select creatures from destination";
        Alert a = new Alert(Alert.AlertType.INFORMATION,
                string,
                ButtonType.OK);
        //createSwapButton(false);
        a.setHeaderText("Use the effect of : " + clientState.getCurrentPlayedCharacter());
        a.setTitle(gameTitle);
        a.showAndWait();
    }
}

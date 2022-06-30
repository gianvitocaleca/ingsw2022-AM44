package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.view.ClientState;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.util.Optional;

import static it.polimi.ingsw.utils.TextAssets.useEffectText;
import static it.polimi.ingsw.view.GUI.GuiAssets.gameTitle;
import static it.polimi.ingsw.view.GUI.GuiAssets.noErrorCode;

/**
 * This class is used to crate alert caused by client's actions.
 */
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
    public static void errorAlert(String string, Alert.AlertType alertType) {
        Alert a = new Alert(alertType,
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
        Alert a = new Alert(Alert.AlertType.INFORMATION,
                string,
                ButtonType.OK);
        a.setTitle(gameTitle);
        a.setHeaderText(useEffectText+ clientState.getCurrentPlayedCharacter());
        a.showAndWait();
    }

    /**
     * Shows the alert to the player to inform the character behaviour
     */
    public static void characterNeedsIslandIndex(ClientState clientState) {
        String string = "Select an island";
        Alert a = new Alert(Alert.AlertType.INFORMATION,
                string,
                ButtonType.OK);
        a.setTitle(gameTitle);
        a.setHeaderText(useEffectText + clientState.getCurrentPlayedCharacter());
        a.showAndWait();
    }

    /**
     * Shows the alert to the player to inform the character behaviour
     */
    public static void characterNeedsSourceCreature(ClientState clientState) {
        String string = "Select a source creature";
        Alert a = new Alert(Alert.AlertType.INFORMATION,
                string,
                ButtonType.OK);
        a.setTitle(gameTitle);
        a.setHeaderText(useEffectText + clientState.getCurrentPlayedCharacter());
        a.showAndWait();
    }

    /**
     * Shows the alert to the player to inform the character behaviour.
     * Creates the swap button.
     */
    public static void characterNeedsSwapCreatures(ClientState clientState) {
        String string = "Select at most " + clientState.getCurrentPlayedCharacter().getMaxMoves()
                + " creatures from the source";
        Alert a = new Alert(Alert.AlertType.INFORMATION,
                string,
                ButtonType.OK);
        a.setHeaderText("Use the effect of : " + clientState.getCurrentPlayedCharacter());
        a.setTitle(gameTitle);
        a.showAndWait();
    }

    /**
     * Creates the second alert for the swap character
     */
    public static void createAlertForSwapCommand(ClientState clientState) {
        String string = "Select creatures from destination";
        Alert a = new Alert(Alert.AlertType.INFORMATION,
                string,
                ButtonType.OK);
        a.setHeaderText("Use the effect of : " + clientState.getCurrentPlayedCharacter());
        a.setTitle(gameTitle);
        a.showAndWait();
    }
}

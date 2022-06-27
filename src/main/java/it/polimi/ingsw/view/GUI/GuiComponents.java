package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.view.ClientState;
import it.polimi.ingsw.model.enums.Creature;
import it.polimi.ingsw.model.player.Assistant;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.network.server.networkMessages.Headers;
import it.polimi.ingsw.network.server.networkMessages.payloads.ShowModelPayload;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static it.polimi.ingsw.utils.TextAssets.*;
import static it.polimi.ingsw.view.GUI.GuiAssets.*;
import static it.polimi.ingsw.view.GUI.GuiAssets.tableTowerHeight;
import static it.polimi.ingsw.view.GUI.GuiCss.*;

public class GuiComponents {

    /**
     * @param p is the given player
     * @return is the tower and header container
     */
    public static VBox createTowerComponent(Player p) {
        HBox towers = towerCounter(p.getMyColor(), p.getTowers());
        if (towers != null) {
            towers.setSpacing(mediumSpacing);
            towers.setAlignment(Pos.CENTER);
        }
        VBox ans = new VBox(towers);
        ans.setAlignment(Pos.CENTER);
        ans.setSpacing(smallSpacing);
        ans.setStyle(defaultComponentLayout);
        return ans;
    }

    /**
     * @param color
     * @param num   is the number of given towers
     * @return is the tower's container and text number
     */
    public static HBox towerCounter(it.polimi.ingsw.model.enums.Color color, int num) {
        ImageView towerImage;
        switch (color) {
            case BLACK:
                towerImage = new ImageView(new Image("Table/black.png"));
                break;
            case GREY:
                towerImage = new ImageView(new Image("Table/grey.png"));
                break;
            case WHITE:
                towerImage = new ImageView(new Image("Table/white.png"));
                break;
            default:
                return null;
        }
        towerImage.setFitWidth(tableTowerWidth);
        towerImage.setFitHeight(tableTowerHeight);
        return counterText(num, towerImage);
    }

    /**
     * @param num   is the text number to be shown
     * @param image is the image to be shown
     * @return is the box of the text and the image
     */
    public static HBox counterText(int num, ImageView image) {
        Text numText = new Text("" + num + "");
        numText.setStyle(bodyFont);
        HBox ans = new HBox(image, numText);
        ans.setSpacing(smallSpacing);
        ans.setAlignment(Pos.CENTER);
        return ans;
    }

    /**
     * @return is the correct creature container
     */
    public static HBox createCreatures(String name, Player p) {
        HBox ans = new HBox();
        List<HBox> creatureCounters = new ArrayList<>();
        switch (name) {
            case entranceHeaderText:
                for (Creature c : Creature.values()) {
                    creatureCounters.add(creatureCounter(c, p.getEntrance().getNumberOfStudentsByCreature(c)));
                }
                break;
            case diningRoomHeaderText:
                for (Creature c : Creature.values()) {
                    creatureCounters.add(creatureCounter(c, p.getDiningRoom().getNumberOfStudentsByCreature(c)));
                }
                break;
            case professorsHeaderText:
                for (Creature c : Creature.values()) {
                    if (p.hasProfessor(c)) {
                        creatureCounters.add(professorsCounter(c));
                    }
                }
                break;

        }
        ans.getChildren().addAll(creatureCounters);

        ans.setAlignment(Pos.CENTER);
        ans.setSpacing(smallSpacing);
        return ans;
    }

    /**
     * @param creature
     * @param num      is the number of given creatures
     * @return is the container of the creature's image and text number
     */
    public static HBox creatureCounter(Creature creature, int num) {
        ImageView creatureImage;
        switch (creature) {
            case RED_DRAGONS:
                creatureImage = new ImageView(new Image("Table/red.png"));
                break;
            case YELLOW_GNOMES:
                creatureImage = new ImageView(new Image("Table/yellow.png"));
                break;
            case BLUE_UNICORNS:
                creatureImage = new ImageView(new Image("Table/blue.png"));
                break;
            case GREEN_FROGS:
                creatureImage = new ImageView(new Image("Table/green.png"));
                break;
            case PINK_FAIRIES:
                creatureImage = new ImageView(new Image("Table/pink.png"));
                break;
            default:
                return null;
        }
        creatureImage.setFitWidth(playerContentWidth);
        creatureImage.setFitHeight(playerContentHeight);
        return counterText(num, creatureImage);
    }

    /**
     * @param creature is the professor's creature
     * @return the professor's container
     */
    public static HBox professorsCounter(Creature creature) {
        ImageView creatureImage;
        switch (creature) {
            case RED_DRAGONS:
                creatureImage = new ImageView(new Image("Table/redProf.png"));
                break;
            case YELLOW_GNOMES:
                creatureImage = new ImageView(new Image("Table/yellowProf.png"));
                break;
            case BLUE_UNICORNS:
                creatureImage = new ImageView(new Image("Table/blueProf.png"));
                break;
            case GREEN_FROGS:
                creatureImage = new ImageView(new Image("Table/greenProf.png"));
                break;
            case PINK_FAIRIES:
                creatureImage = new ImageView(new Image("Table/pinkProf.png"));
                break;
            default:
                return null;
        }
        creatureImage.setFitWidth(playerContentWidth);
        creatureImage.setFitHeight(playerContentHeight);
        HBox ans = new HBox(creatureImage);
        ans.setSpacing(smallSpacing);
        ans.setAlignment(Pos.CENTER);
        return ans;
    }

    /**
     * @param lastPlayedAssistants is the content
     * @return is the assistant component
     */
    public static VBox createComponentWithAssistant(List<Assistant> lastPlayedAssistants) {
        Text assistantTitle = new Text(assistantHeaderText);
        HBox assistant;
        StackPane assistantStack = new StackPane();
        if (lastPlayedAssistants.size() > 0) {
            assistantStack.getChildren().add(new Circle(playerRadius, new ImagePattern(new Image(lastPlayedAssistants.get(lastPlayedAssistants.size() - 1).getName().getAssistant()))));
        } else {
            assistantStack.getChildren().add(new Circle(playerRadius, new ImagePattern(new Image("strawberry.png"))));
        }
        assistant = new HBox(assistantStack);
        assistant.setAlignment(Pos.CENTER);
        VBox assistantBox = new VBox(assistantTitle, assistant);
        assistantBox.setAlignment(Pos.CENTER);
        return assistantBox;
    }

    /**
     * @param coins is the player's number of coins
     * @return is the coin and header container
     */
    public static VBox createCoinComponent(int coins) {
        Text coinTitle = new Text(coinHeaderText);
        Text coinValue = new Text(String.valueOf(coins));
        coinTitle.setStyle(bodyFont);
        coinTitle.setStyle(borderInsets);
        coinValue.setStyle(bodyFont);
        VBox coinBox = new VBox(coinTitle, coinValue);
        coinBox.setAlignment(Pos.CENTER);
        coinBox.setSpacing(smallSpacing);
        coinBox.setStyle(defaultComponentLayout);
        return coinBox;
    }

    /**
     * @return is the bank container box
     */
    public static HBox createBankComponent(int coinReserve) {
        Text title = new Text("Coin Reserve:");
        ImageView coin = new ImageView("moneta.png");
        coin.setFitWidth(coinWidth);
        coin.setFitHeight(coinHeight);
        Text num = new Text("" + coinReserve + "");
        num.setStyle(headerFont);
        HBox coins = new HBox(num, coin);
        coins.setSpacing(mediumSpacing);
        coins.setAlignment(Pos.CENTER);
        HBox ans = new HBox(title, coins);
        ans.setAlignment(Pos.CENTER);
        ans.setSpacing(smallSpacing);
        return ans;
    }

    /**
     * @return is the current player container box
     */
    public static HBox createCurrentPlayerBox(ShowModelPayload modelCache, String MY_USERNAME) {
        Text currentPlayer = new Text();
        String playerText;
        if (modelCache.getCurrentPlayerUsername().equals(MY_USERNAME)) {
            playerText = "It's your turn.";
        } else {
            playerText = "It's " + modelCache.getCurrentPlayerUsername() + "'s turn.";
        }
        currentPlayer.setText(playerText);
        HBox currentPlayerBox = new HBox(currentPlayer);
        currentPlayerBox.setAlignment(Pos.CENTER);
        return currentPlayerBox;
    }

    /**
     * @return is the action phase container box
     */
    public static HBox createActionPhaseBox(ClientState clientState) {
        Text actionPhase = new Text();
        String text = "";
        if (clientState.getHeaders().equals(Headers.action)) {
            if (clientState.isMoveStudents()) {
                text += moveStudentsText;
            } else if (clientState.isMoveMotherNature()) {
                text += moveMotherNatureText;
            } else if (clientState.isSelectCloud()) {
                text += selectCloudText;
            }
            if (clientState.isSelectCharacter()) {
                text += chooseCharacterText;
            }
        } else {
            text += selectAssistantText;
        }
        actionPhase.setText(text);
        HBox actionPhaseBox = new HBox(actionPhase);
        actionPhaseBox.setAlignment(Pos.CENTER);
        return actionPhaseBox;
    }

    /**
     * @return is the current phase container box
     */
    public static HBox createCurrentPhaseBox(ClientState clientState) {
        Text currentPhase = new Text();
        StringProperty header = new SimpleStringProperty(clientState.getHeaders().toString());
        currentPhase.textProperty().bind(header);
        currentPhase.setStyle(borderInsets);
        Text currentPrePhase = new Text(currentPhasePrefix);
        HBox currentPhaseBox = new HBox(currentPrePhase, currentPhase);
        currentPhaseBox.setAlignment(Pos.CENTER);
        return currentPhaseBox;
    }

    /**
     * Creates the creature counter container for a character
     *
     * @param creatures is the list of creatures of the given character
     * @return is the creature counter container
     */
    public static HBox createCharacterCreature(List<Creature> creatures) {
        List<HBox> creatureCounters = new ArrayList<>();
        Map<Creature, Integer> counter = new HashMap<>();
        HBox ans = new HBox();
        for (Creature c : Creature.values()) {
            counter.put(c, 0);
        }
        for (Creature c : creatures) {
            counter.put(c, counter.get(c) + 1);
        }
        for (Creature c : Creature.values()) {
            creatureCounters.add(creatureCounter(c, counter.get(c)));
        }
        ans.getChildren().addAll(creatureCounters);
        ans.setAlignment(Pos.CENTER);
        ans.setSpacing(smallSpacing);
        return ans;
    }

    /**
     * Creates the no entry container for the characters
     *
     * @param noEntry is the number of no entries to show
     * @return is the number of no entries and image container
     */
    public static HBox createNoEntryCharacter(int noEntry) {
        HBox deactivators = new HBox();
        ImageView deactivatorImage = new ImageView(new Image("noEntry.png"));
        deactivatorImage.setFitWidth(playerContentWidth);
        deactivatorImage.setFitHeight(playerContentHeight);
        Text deactivatorNumber = new Text(String.valueOf(noEntry));
        deactivatorNumber.setStyle(bodyFont);
        deactivators.getChildren().addAll(deactivatorImage, deactivatorNumber);
        return deactivators;
    }

    /**
     * Creates the title of the game
     *
     * @return is the Box containing the title
     */
    public static VBox gameTitle() {
        Text title = new Text(gameTitle);
        title.setFont(titleFont);
        title.setFill(Color.DARKVIOLET);
        title.setStroke(Color.LIGHTGOLDENRODYELLOW);
        title.setStrokeWidth(1);
        VBox titleBox = new VBox(title);
        titleBox.setAlignment(Pos.CENTER);
        return titleBox;
    }
}

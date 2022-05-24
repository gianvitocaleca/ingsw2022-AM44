package it.polimi.ingsw.client;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClientGui extends Application {

    private final Color backgroundColor = Color.AQUAMARINE;
    private int numberOfPlayers = 2;
    private boolean advancedRules = false;

    @Override
    public void start(Stage primaryStage) throws IOException {

        //Pane creation
        BorderPane creationRoot = new BorderPane();
        creationRoot.setBackground(new Background(new BackgroundImage(new Image("sfondoCreazione.png"), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
        BorderPane gameRoot = new BorderPane();
        gameRoot.setBackground(new Background(new BackgroundFill(backgroundColor, CornerRadii.EMPTY, Insets.EMPTY)));

        //Scene creation
        Scene creationScene = new Scene(creationRoot);
        Scene gameScene = new Scene(gameRoot);

        //Creation number of players buttons
        Button twoPlayers = new Button("Two players");
        twoPlayers.setFont(Font.font(30));
        Button threePlayers = new Button("Three players");
        threePlayers.setFont(Font.font(30));

        //Creation type of rules buttons
        Button basicRules = new Button("Basic Rules");
        twoPlayers.setFont(Font.font(30));
        Button advancedRules = new Button("Advanced Rules");
        threePlayers.setFont(Font.font(30));
        HBox typeOfRulesButtons = new HBox(basicRules, advancedRules);
        typeOfRulesButtons.setSpacing(5);
        typeOfRulesButtons.setAlignment(Pos.CENTER);

        twoPlayers.setOnMouseClicked(e -> {
            this.numberOfPlayers = 2;
            creationRoot.setCenter(typeOfRulesButtons);
            primaryStage.setFullScreen(true);
            primaryStage.setResizable(false);
        });
        threePlayers.setOnMouseClicked(e -> {
            this.numberOfPlayers = 3;
            creationRoot.setCenter(typeOfRulesButtons);
            primaryStage.setFullScreen(true);
            primaryStage.setResizable(false);
        });

        basicRules.setOnMouseClicked(e -> {
            this.advancedRules = false;
            gameSceneGenerator(gameRoot, numberOfPlayers);
            primaryStage.setScene(gameScene);
            primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
            primaryStage.setFullScreen(true);
            primaryStage.setResizable(false);
        });
        advancedRules.setOnMouseClicked(e -> {
            this.advancedRules = true;
            gameSceneGenerator(gameRoot, numberOfPlayers);
            primaryStage.setScene(gameScene);
            primaryStage.setResizable(false);
            primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
            primaryStage.setFullScreen(true);

        });


        HBox numOfPlayersButtons = new HBox(twoPlayers, threePlayers);
        numOfPlayersButtons.setSpacing(5);
        numOfPlayersButtons.setAlignment(Pos.CENTER);

        Text title = new Text("Eryantis");
        title.setFont(Font.font("Papyrus", FontWeight.LIGHT, 160));
        title.setFill(Color.DARKVIOLET);
        title.setStroke(Color.LIGHTGOLDENRODYELLOW);
        title.setStrokeWidth(1);
        VBox titleBox = new VBox(title);
        titleBox.setAlignment(Pos.CENTER);
        creationRoot.setTop(titleBox);
        //Game components

        creationRoot.setCenter(numOfPlayersButtons);
        primaryStage.setScene(creationScene);
        primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        primaryStage.setFullScreen(true);
        primaryStage.setTitle("Eryantis");
        primaryStage.show();
    }

    public void prova(){
        System.out.println("Prova");
    }

    private void gameSceneGenerator(BorderPane gamePane, int numberOfPlayers) {
        gamePane.setLeft(otherPlayerGenerator());
        gamePane.setBottom(myPlayerGenerator());
        Group islands = new Group();
        Image island = new Image("island.png");
        List<ImageView> islandView = new ArrayList<>();
        int numberOfIslands = 12;
        int centerX = 100;
        int centerY = 100;
        int radius = 300;
        for (int i = 0; i < numberOfIslands; i++) {
            ImageView islandImageView = new ImageView(island);
            islandImageView.setX(centerX + radius * Math.cos(2 * Math.PI * i / numberOfIslands));
            islandImageView.setY(centerY + radius * Math.sin(2 * Math.PI * i / numberOfIslands));
            islandView.add(islandImageView);
        }
        islands.getChildren().addAll(islandView);
        gamePane.setCenter(islands);
        if (numberOfPlayers == 3) {
            gamePane.setRight(otherPlayerGenerator());
        }
        if (advancedRules) {
            gamePane.setTop(new Text("Advanced Rules"));
        } else {
            gamePane.setTop(new Text("Basic Rules"));
        }

    }

    private VBox otherPlayerGenerator() {
        Text username = new Text("Username");
        Text wizard = new Text("Wizard");
        Text color = new Text("Color");
        ImageView gameBoard = new ImageView(new Image("plancia.png"));
        gameBoard.setFitHeight(460);
        gameBoard.setFitWidth(200);
        VBox player = new VBox(username, wizard, gameBoard, color);
        player.setSpacing(5);
        player.setAlignment(Pos.CENTER);
        return player;
    }

    private HBox myPlayerGenerator() {
        Text username = new Text("Username");
        Text wizard = new Text("Wizard");
        Text color = new Text("Color");
        ImageView gameBoard = new ImageView(new Image("plancia2.png"));
        gameBoard.setFitHeight(200);
        gameBoard.setFitWidth(460);
        HBox player = new HBox(username, wizard, gameBoard, color);
        player.setSpacing(5);
        player.setAlignment(Pos.CENTER);
        return player;
    }
}



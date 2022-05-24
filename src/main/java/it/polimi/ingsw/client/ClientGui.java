package it.polimi.ingsw.client;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClientGui extends Application {

    private final Color backgroundColor = Color.AQUAMARINE;

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
        //Creation components
        Button twoPlayers = new Button("Two players");
        twoPlayers.setFont(Font.font(30));
        Button threePlayers = new Button("Three players");
        threePlayers.setFont(Font.font(30));
        twoPlayers.setOnMouseClicked(e -> {
            gameSceneGenerator(gameRoot, 2);
            primaryStage.setScene(gameScene);
            primaryStage.setMaximized(true);
            primaryStage.setResizable(false);
        });
        threePlayers.setOnMouseClicked(e -> {
            gameSceneGenerator(gameRoot, 3);
            primaryStage.setScene(gameScene);
            primaryStage.setMaximized(true);
            primaryStage.setResizable(false);
        });
        HBox buttons = new HBox(twoPlayers, threePlayers);
        buttons.setSpacing(5);
        buttons.setAlignment(Pos.CENTER);

        Text title = new Text("Eryantis");
        title.setFont(Font.font("Papyrus", FontWeight.LIGHT, 160));
        title.setFill(Color.DARKVIOLET);
        title.setStroke(Color.LIGHTGOLDENRODYELLOW);
        title.setStrokeWidth(1);
        VBox titleBox = new VBox(title);
        titleBox.setAlignment(Pos.CENTER);
        creationRoot.setTop(titleBox);
        //Game components

        creationRoot.setCenter(buttons);
        primaryStage.setScene(creationScene);
        primaryStage.setMaximized(true);
        primaryStage.setTitle("Eryantis");
        primaryStage.show();
    }

    private void gameSceneGenerator(BorderPane gamePane, int numberOfPlayers) {
        gamePane.setLeft(playerPaneGenerator(false));
        gamePane.setBottom(playerPaneGenerator(true));
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
            gamePane.setRight(playerPaneGenerator(false));
        }

    }

    private GridPane playerPaneGenerator(boolean isMe) {
        GridPane player1Pane = new GridPane();
        player1Pane.setAlignment(Pos.CENTER);
        player1Pane.setMinSize(50, 100);
        player1Pane.setPadding(new Insets(10, 10, 10, 10));
        player1Pane.setVgap(5);
        player1Pane.add(new Text("Username"), 0, 0);
        player1Pane.add(new Text("Mago"), 0, 1);
        player1Pane.add(new Text("Colore"), 0, 4);
        ImageView imageView;
        if (isMe) {
            imageView = new ImageView(new Image("plancia2.png"));
        } else {
            imageView = new ImageView(new Image("plancia.png"));
        }
        imageView.setFitHeight(460);
        imageView.setFitWidth(200);
        player1Pane.add(imageView, 0, 3);
        return player1Pane;
    }
}



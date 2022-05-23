package it.polimi.ingsw.client;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClientGui extends Application {
    private ScreenController screenController;

    @Override
    public void start(Stage primaryStage) throws IOException {
        Scene creationScene = creationScene(new MessageGUI());
        creationScene.setFill(Color.BLUE);
        primaryStage.setScene(creationScene);
        primaryStage.setMaximized(true);
        primaryStage.setTitle("Eryantis");


        screenController = new ScreenController(creationScene);

        primaryStage.show();
    }

    private Scene creationScene(MessageGUI message) {

        GridPane creationPane = new GridPane();
        Scene creationScene = new Scene(creationPane);
        //creationScene.

        String result = message.getMessaggio();
        List<String> results = List.of(result.split(":"));
        List<String> values = List.of(results.get(1).split(","));


        Text text = new Text(results.get(0));
        ChoiceBox numberOfPlayers = new ChoiceBox();
        numberOfPlayers.getItems().addAll(values.get(0), values.get(1));
        Button buttonRegister = new Button("Confirm");

        creationPane.add(text, 0, 0);
        creationPane.add(numberOfPlayers, 0, 1);
        creationPane.add(buttonRegister, 0, 2);
        buttonRegister.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                screenController.addScreen("gamePane", gamePane(Integer.parseInt(numberOfPlayers.getValue().toString())));
                screenController.activate("gamePane");
            }
        });
        return creationScene;
    }

    private GridPane gamePane(int numberOfPlayers) {
        GridPane gameDispositionPane = new GridPane();

        gameDispositionPane.setMinSize(1920, 1080);
        gameDispositionPane.setPadding(new Insets(10, 10, 10, 10));
        gameDispositionPane.setVgap(5);
        gameDispositionPane.add(playerPaneGenerator(), 0, 0);
        Group islands = new Group();
        gameDispositionPane.add(islands, 1, 0);
        Image island = new Image("island.png");
        List<ImageView> islandView = new ArrayList<>();
        islandView.add(new ImageView(island));
        islandView.add(new ImageView(island));
        islandView.add(new ImageView(island));
        int j = 0;
        for (ImageView i : islandView) {
            i.setX(0 + j);
            i.setY(0 + j);
            islands.getChildren().add(i);

            j += 50;

        }

        if (numberOfPlayers == 3) {
            gameDispositionPane.add(playerPaneGenerator(), 2, 0);
        }


        return gameDispositionPane;
    }

    private GridPane playerPaneGenerator() {
        GridPane player1Pane = new GridPane();
        player1Pane.setMinSize(50, 100);
        player1Pane.setPadding(new Insets(10, 10, 10, 10));
        player1Pane.setVgap(5);
        player1Pane.add(new Text("Username"), 0, 0);
        player1Pane.add(new Text("Mago"), 0, 1);
        player1Pane.add(new Text("Colore"), 0, 4);
        ImageView imageView = new ImageView(new Image("plancia.png"));
        imageView.setFitHeight(460);
        imageView.setFitWidth(200);
        player1Pane.add(imageView, 0, 3);
        return player1Pane;
    }
}

package it.polimi.ingsw.client;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClientGui extends Application {
    private static String address;
    private static int port;
    private final Color backgroundColor = Color.rgb(131, 187, 218);
    private int numberOfPlayers = 2;
    private boolean advancedRules = false;

    private String gameTitle = "Eryantis";
    private Stage stage;
    private BorderPane root;
    private AbstractSender client;
    private Thread senderThread;

    @Override
    public void start(Stage primaryStage) throws IOException {
        stage = primaryStage;

        //Start the sender component
        startSender();

        //Pane creation
        root = new BorderPane();

        //Scene creation
        Scene mainScene = new Scene(root);

        //Game Title
        root.setTop(gameTitle());

        //Game creation phase
        creationMechanics();

        //Game components


        primaryStage.setMaximized(true);
        primaryStage.setScene(mainScene);
        primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        primaryStage.setTitle(gameTitle);
        primaryStage.show();
        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest(e -> {
            e.consume();
            quitStage();
        });
    }

    private void startSender() {
        client = new ConcreteGUISender(address, port, this);
        senderThread = new Thread(() -> {
            try {
                client.startClient();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        senderThread.start();
    }

    private VBox gameTitle() {
        Text title = new Text(gameTitle);
        title.setFont(Font.font("Papyrus", FontWeight.LIGHT, 160));
        title.setFill(Color.DARKVIOLET);
        title.setStroke(Color.LIGHTGOLDENRODYELLOW);
        title.setStrokeWidth(1);
        VBox titleBox = new VBox(title);
        titleBox.setAlignment(Pos.CENTER);
        return titleBox;
    }

    private void creationMechanics() {
        //Background
        root.setBackground(new Background(new BackgroundImage(new Image("sfondoCreazione.png"), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));

        //Creation number of players buttons
        Button twoPlayers = new Button("Two players");
        twoPlayers.setFont(Font.font(30));
        Button threePlayers = new Button("Three players");
        threePlayers.setFont(Font.font(30));
        HBox numOfPlayersButtons = new HBox(twoPlayers, threePlayers);
        numOfPlayersButtons.setSpacing(5);
        numOfPlayersButtons.setAlignment(Pos.CENTER);

        //Creation type of rules buttons
        Button basicRules = new Button("Basic Rules");
        basicRules.setFont(Font.font(30));
        Button advancedRules = new Button("Advanced Rules");
        advancedRules.setFont(Font.font(30));
        HBox typeOfRulesButtons = new HBox(basicRules, advancedRules);
        typeOfRulesButtons.setSpacing(5);
        typeOfRulesButtons.setAlignment(Pos.CENTER);

        twoPlayers.setOnAction(e -> {
            this.numberOfPlayers = 2;
            root.setCenter(typeOfRulesButtons);
        });
        threePlayers.setOnAction(e -> {
            this.numberOfPlayers = 3;
            root.setCenter(typeOfRulesButtons);
        });

        basicRules.setOnAction(e -> {
            this.advancedRules = false;
            gamePaneGenerator();
        });
        advancedRules.setOnAction(e -> {
            this.advancedRules = true;
            gamePaneGenerator();

        });

        //first buttons to appear
        root.setCenter(numOfPlayersButtons);
    }

    public void prova() {
        System.out.println("Prova");
    }

    private void gamePaneGenerator() {
        root.setBackground(new Background(new BackgroundFill(backgroundColor, CornerRadii.EMPTY, Insets.EMPTY)));
        root.setLeft(otherPlayerGenerator());
        root.setBottom(myPlayerGenerator());
        //Island creation
        Group table = new Group();
        Image island = new Image("new_island.png");
        List<ImageView> islandView = new ArrayList<>();
        int numberOfIslands = 12;
        int centerX = 100;
        int centerY = 100;
        int radius = 300;
        for (int i = 0; i < numberOfIslands; i++) {
            ImageView islandImageView = new ImageView(island);
            islandImageView.setFitWidth(103);
            islandImageView.setFitHeight(100);
            islandImageView.setX(centerX + radius * Math.cos(2 * Math.PI * i / numberOfIslands));
            islandImageView.setY(centerY + radius * Math.sin(2 * Math.PI * i / numberOfIslands));
            islandView.add(islandImageView);
        }
        table.getChildren().addAll(islandView);

        //Cloud creation
        Image cloud = new Image("cloud.png");
        List<ImageView> cloudView = new ArrayList<>();
        int cloudRadius = 100;
        for (int i = 0; i < numberOfPlayers; i++) {
            ImageView cloudImageView = new ImageView(cloud);
            cloudImageView.setFitHeight(100);
            cloudImageView.setFitWidth(100);
            cloudImageView.setX(centerX + cloudRadius * Math.cos(2 * Math.PI * i / numberOfPlayers));
            cloudImageView.setY(centerY + cloudRadius * Math.sin(2 * Math.PI * i / numberOfPlayers));
            cloudView.add(cloudImageView);
        }
        table.getChildren().addAll(cloudView);

        root.setCenter(table);
        if (numberOfPlayers == 3) {
            root.setRight(otherPlayerGenerator());
        }
        Text typeOfRules;
        if (advancedRules) {
            typeOfRules = new Text("Advanced Rules");
        } else {
            typeOfRules = new Text("Basic Rules");
        }
        typeOfRules.setTextAlignment(TextAlignment.CENTER);
        typeOfRules.setFont(Font.font(30));
        Button quit = new Button("Quit Game");
        quit.setFont(Font.font(30));
        quit.setOnAction(e -> quitStage());
        HBox topMenu = new HBox(typeOfRules, quit);
        topMenu.setAlignment(Pos.CENTER);
        topMenu.setSpacing(50);
        root.setTop(topMenu);
    }

    private VBox otherPlayerGenerator() {
        Text username = new Text("Username");
        Text wizard = new Text("Wizard");
        Text color = new Text("Color");
        VBox player = new VBox(username, wizard,
                createComponentWithCreatures("Entrance"),
                createComponentWithCreatures("Dining Room"),
                createComponentWithCreatures("Professors"),
                createTowerComponent(), color);
        player.setSpacing(5);
        player.setAlignment(Pos.CENTER);
        return player;
    }

    private HBox myPlayerGenerator() {
        Text username = new Text("Username");
        Text wizard = new Text("Wizard");
        Text color = new Text("Color");
        HBox player = new HBox(username, wizard,
                createComponentWithCreatures("Entrance"),
                createComponentWithCreatures("Dining Room"),
                createComponentWithCreatures("Professors"),
                createTowerComponent(), color);
        player.setSpacing(5);
        player.setAlignment(Pos.CENTER);
        return player;
    }

    private HBox createCreatures() {
        Rectangle redCreature = new Rectangle(50, 50, Color.RED);
        redCreature.setStroke(Color.BLACK);
        redCreature.setStrokeWidth(2);
        Rectangle greenCreature = new Rectangle(50, 50, Color.GREEN);
        greenCreature.setStroke(Color.BLACK);
        greenCreature.setStrokeWidth(2);
        Rectangle yellowCreature = new Rectangle(50, 50, Color.YELLOW);
        yellowCreature.setStroke(Color.BLACK);
        yellowCreature.setStrokeWidth(2);
        Rectangle blueCreature = new Rectangle(50, 50, Color.BLUE);
        blueCreature.setStroke(Color.BLACK);
        blueCreature.setStrokeWidth(2);
        Rectangle pinkCreature = new Rectangle(50, 50, Color.PINK);
        pinkCreature.setStroke(Color.BLACK);
        pinkCreature.setStrokeWidth(2);
        return new HBox(redCreature, greenCreature, yellowCreature, blueCreature, pinkCreature);
    }

    private VBox createComponentWithCreatures(String name) {
        Text title = new Text(name);
        VBox ans = new VBox(title, createCreatures());
        ans.setAlignment(Pos.CENTER);
        ans.setSpacing(5);
        return ans;
    }

    private VBox createTowerComponent() {
        Text title = new Text("Towers");
        Rectangle towers = new Rectangle(250, 50);
        towers.setFill(Color.BLACK);
        VBox ans = new VBox(title, towers);
        ans.setAlignment(Pos.CENTER);
        ans.setSpacing(5);
        return ans;
    }

    public static void setAddress(String address) {
        ClientGui.address = address;
    }

    public static void setPort(int port) {
        ClientGui.port = port;
    }

    public void quitStage() {
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
        }
    }
}



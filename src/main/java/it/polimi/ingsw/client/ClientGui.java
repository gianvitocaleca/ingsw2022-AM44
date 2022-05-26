package it.polimi.ingsw.client;

import it.polimi.ingsw.server.model.enums.Creature;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
import java.util.*;

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

    private Queue<String> guiEvents;
    private String cssLayout = "-fx-border-color: black;\n" +
            "-fx-border-insets: 5;\n" +
            "-fx-border-width: 3;\n";

    @Override
    public void start(Stage primaryStage) throws IOException {
        stage = primaryStage;

        //Pane creation
        root = new BorderPane();

        //Scene creation
        Scene mainScene = new Scene(root);

        //Game Title
        root.setTop(gameTitle());

        //Join screen
        root.setCenter(joinButton());

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

    private Button joinButton() {
        //Background
        root.setBackground(new Background(new BackgroundImage(new Image("sfondoCreazione.png"), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
        //first buttons to appear
        //Join button
        Button join = new Button("Join");
        join.setFont(Font.font(30));
        join.setOnAction(e -> {
            //Start the sender component
            startSender();
        });
        return join;
    }

    private void startSender() {
        guiEvents = new LinkedList<>();
        client = new ConcreteGUISender(address, port, this, guiEvents);
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

    public void numberOfPlayers(){
        //Creation number of players buttons
        Button twoPlayers = new Button("Two players");
        twoPlayers.setFont(Font.font(30));
        Button threePlayers = new Button("Three players");
        threePlayers.setFont(Font.font(30));
        HBox numOfPlayersButtons = new HBox(twoPlayers, threePlayers);
        numOfPlayersButtons.setSpacing(5);
        numOfPlayersButtons.setAlignment(Pos.CENTER);

        twoPlayers.setOnAction(e -> {
            this.numberOfPlayers = 2;
            guiEvents.add("2");
        });
        threePlayers.setOnAction(e -> {
            this.numberOfPlayers = 3;
            guiEvents.add("3");
        });

        root.setCenter(numOfPlayersButtons);
    }

    public void typeOfRules() {

        //Creation type of rules buttons
        Button basicRules = new Button("Basic Rules");
        basicRules.setFont(Font.font(30));
        Button advancedRules = new Button("Advanced Rules");
        advancedRules.setFont(Font.font(30));
        HBox typeOfRulesButtons = new HBox(basicRules, advancedRules);
        typeOfRulesButtons.setSpacing(5);
        typeOfRulesButtons.setAlignment(Pos.CENTER);

        basicRules.setOnAction(e -> {
            this.advancedRules = false;
            guiEvents.add("0");
        });
        advancedRules.setOnAction(e -> {
            this.advancedRules = true;
            guiEvents.add("1");
        });

        root.setCenter(typeOfRulesButtons);
    }

    public void loginUsername(){
        Text text = new Text("Provide your username");
        text.setFont(Font.font(20));
        TextField prompt = new TextField("");
        prompt.setMaxWidth(200);
        prompt.setOnAction(e -> {
            guiEvents.add(prompt.getCharacters().toString());
        });

        VBox box = new VBox(text, prompt);
        box.setSpacing(5);
        box.setAlignment(Pos.CENTER);

        root.setCenter(box);
    }

    public void color(){
        Text text = new Text("Choose a color");
        text.setFont(Font.font(20));
        ChoiceBox<String> prompt = new ChoiceBox<>();
        prompt.getItems().addAll("White","Black","Gray");
        Button button = new Button();
        button.setText("Confirm");
        button.setOnAction(e -> {
            switch (prompt.getValue()){
                case "White":
                    guiEvents.add("1");
                    break;
                case "Black":
                    guiEvents.add("2");
                    break;
                case "Gray":
                    guiEvents.add("3");
                    break;
            }
            //button.setDisable(true);
        });

        HBox hBox = new HBox(prompt,button);
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER);
        VBox box = new VBox(text,hBox);
        box.setSpacing(5);
        box.setAlignment(Pos.CENTER);

        root.setCenter(box);
    }

    public void wizard(){
        Text text = new Text("Pick a wizard");
        text.setFont(Font.font(20));
        ChoiceBox<String> prompt = new ChoiceBox<>();
        prompt.getItems().addAll("Gandalf","Baljeet","Sabrina","Kenji");
        Button button = new Button();
        button.setText("Confirm");
        button.setOnAction(e -> {
            switch (prompt.getValue()){
                case "Gandalf":
                    guiEvents.add("1");
                    break;
                case "Baljeet":
                    guiEvents.add("2");
                    break;
                case "Sabrina":
                    guiEvents.add("3");
                    break;
                case "Kenji":
                    guiEvents.add("4");
                    break;
            }
            //button.setDisable(true);
        });

        HBox hBox = new HBox(prompt,button);
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER);
        VBox box = new VBox(text,hBox);
        box.setSpacing(5);
        box.setAlignment(Pos.CENTER);

        root.setCenter(box);
    }

    public void errorAlert(String string){
        Alert a = new Alert(Alert.AlertType.ERROR,
                string,
                ButtonType.OK);
        a.setTitle(gameTitle);
        a.setHeaderText("Error!");
        a.showAndWait();
    }


    public void gamePaneGenerator() {
        root.setBackground(new Background(new BackgroundFill(backgroundColor, CornerRadii.EMPTY, Insets.EMPTY)));
        root.setLeft(opponentsGenerator());
        //root.setLeft(otherPlayerGenerator());
        root.setBottom(myPlayerGenerator());
        //Island creation
        Group table = new Group();
        Image island = new Image("new_island.png");
        ImageView motherNature = new ImageView(new Image("Table/motherNature.png"));
        motherNature.setFitWidth(20);
        motherNature.setFitHeight(28);
        List<StackPane> islands = new ArrayList<>();
        int numberOfIslands = 12;
        int motherNaturePosition = 4;
        int centerX = 100;
        int centerY = 100;
        int radius = 300;
        for (int i = 0; i < numberOfIslands; i++) {
            ImageView islandImageView = new ImageView(island);
            islandImageView.setFitWidth(134);
            islandImageView.setFitHeight(130);
            StackPane islandStack;
            if (i == motherNaturePosition) {
                islandStack = new StackPane(islandImageView, islandComponents(), motherNature);
            } else {
                islandStack = new StackPane(islandImageView, islandComponents());
            }
            islandStack.relocate(centerX + radius * Math.cos(2 * Math.PI * i / numberOfIslands), centerY + radius * Math.sin(2 * Math.PI * i / numberOfIslands));
            islands.add(islandStack);
        }
        table.getChildren().addAll(islands);


        //Cloud creation
        Image cloud = new Image("cloud.png");
        List<ImageView> cloudView = new ArrayList<>();
        int cloudRadius = 100;
        for (int i = 0; i < numberOfPlayers; i++) {
            ImageView cloudImageView = new ImageView(cloud);
            cloudImageView.setFitHeight(130);
            cloudImageView.setFitWidth(130);
            cloudImageView.setX(centerX + cloudRadius * Math.cos(2 * Math.PI * i / numberOfPlayers));
            cloudImageView.setY(centerY + cloudRadius * Math.sin(2 * Math.PI * i / numberOfPlayers));
            cloudView.add(cloudImageView);
        }
        table.getChildren().addAll(cloudView);

        root.setCenter(table);

        setRight();

        setTop();
    }

    private Group islandComponents() {
        Group ans = new Group();
        List<HBox> hboxComponents = new ArrayList<>();
        int radius = 40;
        int numOfComponents = 6;
        int i = 0;
        for (Creature c : Creature.values()) {
            HBox creature = creatureCounter(c, 0);
            creature.relocate(radius * Math.cos(2 * Math.PI * i / numOfComponents), radius * Math.sin(2 * Math.PI * i / numOfComponents));
            hboxComponents.add(creature);
            i++;
        }
        HBox tower = towerCounter(it.polimi.ingsw.server.model.enums.Color.BLACK, 0);
        tower.relocate(radius * Math.cos(2 * Math.PI * i / numOfComponents), radius * Math.sin(2 * Math.PI * i / numOfComponents));
        hboxComponents.add(tower);
        ans.getChildren().addAll(hboxComponents);
        return ans;
    }

    private void setRight() {
        Text charsText = new Text("Available characters");
        charsText.setFont(Font.font(20));

        ImageView firstChar = new ImageView(new Image("Characters/monk.jpg"));
        firstChar.setFitWidth(133);
        firstChar.setFitHeight(200);
        ImageView secondChar = new ImageView(new Image("Characters/fungaro.jpg"));
        secondChar.setFitWidth(133);
        secondChar.setFitHeight(200);
        ImageView thirdChar = new ImageView(new Image("Characters/knight.jpg"));
        thirdChar.setFitWidth(133);
        thirdChar.setFitHeight(200);

        HBox characters = new HBox(firstChar, secondChar, thirdChar);
        characters.setSpacing(5);
        characters.setAlignment(Pos.CENTER);

        Text creatureText = new Text("Creatures");
        creatureText.setFont(Font.font(20));

        ImageView red = new ImageView(new Image("Table/red.png"));
        red.setFitWidth(100);
        red.setFitHeight(100);
        ImageView yellow = new ImageView(new Image("Table/yellow.png"));
        yellow.setFitWidth(100);
        yellow.setFitHeight(100);
        ImageView blue = new ImageView(new Image("Table/blue.png"));
        blue.setFitWidth(100);
        blue.setFitHeight(100);
        ImageView pink = new ImageView(new Image("Table/pink.png"));
        pink.setFitWidth(100);
        pink.setFitHeight(100);
        ImageView green = new ImageView(new Image("Table/green.png"));
        green.setFitWidth(100);
        green.setFitHeight(100);

        HBox creatures = new HBox(red, blue, yellow, pink, green);
        creatures.setSpacing(5);
        creatures.setAlignment(Pos.CENTER);

        Text assistantsText = new Text("Available Assistants");
        assistantsText.setFont(Font.font(20));

        ImageView cheetah = new ImageView(new Image("Assistants/cheetah.png"));
        cheetah.setFitWidth(100);
        cheetah.setFitHeight(146);
        ImageView ostrich = new ImageView(new Image("Assistants/ostrich.png"));
        ostrich.setFitWidth(100);
        ostrich.setFitHeight(146);
        ImageView cat = new ImageView(new Image("Assistants/cat.png"));
        cat.setFitWidth(100);
        cat.setFitHeight(146);
        ImageView eagle = new ImageView(new Image("Assistants/eagle.png"));
        eagle.setFitWidth(100);
        eagle.setFitHeight(146);
        ImageView fox = new ImageView(new Image("Assistants/fox.png"));
        fox.setFitWidth(100);
        fox.setFitHeight(146);
        ImageView lizard = new ImageView(new Image("Assistants/lizard.png"));
        lizard.setFitWidth(100);
        lizard.setFitHeight(146);
        ImageView octopus = new ImageView(new Image("Assistants/octopus.png"));
        octopus.setFitWidth(100);
        octopus.setFitHeight(146);
        ImageView dog = new ImageView(new Image("Assistants/dog.png"));
        dog.setFitWidth(100);
        dog.setFitHeight(146);
        ImageView elephant = new ImageView(new Image("Assistants/elephant.png"));
        elephant.setFitWidth(100);
        elephant.setFitHeight(146);
        ImageView turtle = new ImageView(new Image("Assistants/turtle.png"));
        turtle.setFitWidth(100);
        turtle.setFitHeight(146);

        HBox assistantsHigh = new HBox(cheetah, ostrich, cat, eagle, fox);
        assistantsHigh.setSpacing(5);
        HBox assistantsLow = new HBox(lizard, octopus, dog, elephant, turtle);
        assistantsLow.setSpacing(5);
        VBox assistants = new VBox(assistantsHigh, assistantsLow);
        assistants.setSpacing(5);
        assistants.setAlignment(Pos.CENTER);

        VBox interactiveAssets = new VBox(charsText, characters, creatureText, creatures, assistantsText, assistants);
        interactiveAssets.setSpacing(5);
        interactiveAssets.setAlignment(Pos.CENTER);
        root.setRight(interactiveAssets);

    }

    private void setTop() {
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

    private VBox opponentsGenerator() {
        VBox ans;
        if (numberOfPlayers == 2) {
            ans = new VBox(otherPlayerGenerator());
        } else {
            ans = new VBox(otherPlayerGenerator(), otherPlayerGenerator());
        }
        ans.setSpacing(5);
        return ans;
    }

    private VBox otherPlayerGenerator() {
        Text username = new Text("Username");
        username.setFont(Font.font(20));
        ImageView wizard = new ImageView(new Image("Wizards/gandalf.png"));
        wizard.setFitHeight(50);
        wizard.setFitWidth(33);
        HBox playerInfo = new HBox(wizard, username);
        playerInfo.setAlignment(Pos.CENTER);
        playerInfo.setSpacing(10);
        VBox player = new VBox(playerInfo,
                createComponentWithCreatures("Entrance"),
                createComponentWithCreatures("Dining Room"),
                createComponentWithCreatures("Professors"),
                createTowerComponent());
        player.setSpacing(5);
        player.setAlignment(Pos.CENTER);
        player.setStyle(cssLayout);
        return player;
    }

    private HBox myPlayerGenerator() {
        Text username = new Text("Username");
        username.setFont(Font.font(20));
        ImageView wizard = new ImageView(new Image("Wizards/gandalf.png"));
        wizard.setFitHeight(50);
        wizard.setFitWidth(33);
        HBox playerInfo = new HBox(wizard, username);
        playerInfo.setSpacing(10);
        playerInfo.setAlignment(Pos.CENTER);
        HBox player = new HBox(playerInfo,
                createComponentWithCreatures("Entrance"),
                createComponentWithCreatures("Dining Room"),
                createComponentWithCreatures("Professors"),
                createTowerComponent());
        player.setSpacing(5);
        player.setAlignment(Pos.CENTER);
        player.setStyle(cssLayout);
        return player;
    }

    private HBox creatureCounter(Creature creature, int num) {
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
        creatureImage.setFitWidth(20);
        creatureImage.setFitHeight(20);
        Text numText = new Text("" + num + "");
        numText.setFont(Font.font(20));
        HBox ans = new HBox(creatureImage, numText);
        ans.setSpacing(5);
        ans.setAlignment(Pos.CENTER);
        return ans;
    }

    private HBox towerCounter(it.polimi.ingsw.server.model.enums.Color color, int num) {
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
        towerImage.setFitWidth(20);
        towerImage.setFitHeight(40);
        Text numText = new Text("" + num + "");
        numText.setFont(Font.font(20));
        HBox ans = new HBox(towerImage, numText);
        ans.setSpacing(5);
        ans.setAlignment(Pos.CENTER);
        return ans;
    }

    private HBox createCreatures() {
        HBox ans = new HBox(
                creatureCounter(Creature.RED_DRAGONS, 0),
                creatureCounter(Creature.YELLOW_GNOMES, 0),
                creatureCounter(Creature.BLUE_UNICORNS, 0),
                creatureCounter(Creature.GREEN_FROGS, 0),
                creatureCounter(Creature.PINK_FAIRIES, 0));
        ans.setAlignment(Pos.CENTER);
        ans.setSpacing(5);
        return ans;
    }

    private VBox createComponentWithCreatures(String name) {
        Text title = new Text(name);
        title.setFont(Font.font(20));
        VBox ans = new VBox(title, createCreatures());
        ans.setAlignment(Pos.CENTER);
        ans.setSpacing(5);
        ans.setStyle(cssLayout);
        return ans;
    }

    private VBox createTowerComponent() {
        Text title = new Text("Towers");
        title.setFont(Font.font(20));
        HBox towers = towerCounter(it.polimi.ingsw.server.model.enums.Color.WHITE, 6);
        towers.setSpacing(10);
        towers.setAlignment(Pos.CENTER);
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



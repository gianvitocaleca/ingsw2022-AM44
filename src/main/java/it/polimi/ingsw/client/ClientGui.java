package it.polimi.ingsw.client;

import it.polimi.ingsw.server.CharacterInformation;
import it.polimi.ingsw.server.model.enums.Assistants;
import it.polimi.ingsw.server.model.enums.Creature;
import it.polimi.ingsw.server.model.player.Assistant;
import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.server.model.studentcontainers.Island;
import it.polimi.ingsw.server.networkMessages.Headers;
import it.polimi.ingsw.server.networkMessages.ShowModelPayload;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
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

    private final String gameTitle = "Eryantis";
    private Stage stage;
    private BorderPane root;
    private AbstractSender client;
    private Thread senderThread;
    private String MY_USERNAME;
    private Queue<String> guiEvents;
    private final String cssLayout =
            "-fx-border-color: black;\n" +
                    "-fx-border-insets: 5;\n" +
                    "-fx-border-width: 3;\n";
    private final String rootLayout = "-fx-font-size: 20;";
    private ClientState clientState;
    private StringProperty header;
    private final String borderUnselected = "-fx-border-color: gray; -fx-border-width: 5;";
    private final String borderSelected = "-fx-border-color: black; -fx-border-width: 5;";
    private final String noBorder = "-fx-border-color: none;";

    @Override
    public void start(Stage primaryStage) throws IOException {
        stage = primaryStage;

        //Pane creation
        root = new BorderPane();
        root.setStyle(rootLayout);

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

    public void numberOfPlayers() {
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
            guiEvents.add("0");
        });
        advancedRules.setOnAction(e -> {
            guiEvents.add("1");
        });

        root.setCenter(typeOfRulesButtons);
    }

    public void loginUsername() {
        Text text = new Text("Provide your username");
        text.setFont(Font.font(20));
        TextField prompt = new TextField("");
        prompt.setMaxWidth(200);
        prompt.setOnAction(e -> {
            guiEvents.add(prompt.getCharacters().toString());
            MY_USERNAME = prompt.getCharacters().toString().toLowerCase();
        });

        VBox box = new VBox(text, prompt);
        box.setSpacing(5);
        box.setAlignment(Pos.CENTER);

        root.setCenter(box);
    }

    public void color() {
        Text text = new Text("Choose a color");
        text.setFont(Font.font(20));
        ChoiceBox<String> prompt = new ChoiceBox<>();
        prompt.getItems().addAll("White", "Black", "Gray");
        Button button = new Button();
        button.setText("Confirm");
        button.setOnAction(e -> {
            switch (prompt.getValue()) {
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

        HBox hBox = new HBox(prompt, button);
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER);
        VBox box = new VBox(text, hBox);
        box.setSpacing(5);
        box.setAlignment(Pos.CENTER);

        root.setCenter(box);
    }

    public void wizard() {
        Text text = new Text("Pick a wizard");
        text.setFont(Font.font(20));
        ChoiceBox<String> prompt = new ChoiceBox<>();
        prompt.getItems().addAll("Gandalf", "Baljeet", "Sabrina", "Kenji");
        Button button = new Button();
        button.setText("Confirm");
        button.setOnAction(e -> {
            switch (prompt.getValue()) {
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

        HBox hBox = new HBox(prompt, button);
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER);
        VBox box = new VBox(text, hBox);
        box.setSpacing(5);
        box.setAlignment(Pos.CENTER);

        root.setCenter(box);
    }

    public void errorAlert(String string) {
        Alert a = new Alert(Alert.AlertType.ERROR,
                string,
                ButtonType.OK);
        a.setTitle(gameTitle);
        a.setHeaderText("Error!");
        a.showAndWait();
    }

    public void gamePaneGenerator(ShowModelPayload modelCache, Headers currentHeader) {
        root.setBackground(new Background(new BackgroundFill(backgroundColor, CornerRadii.EMPTY, Insets.EMPTY)));
        //root.setLeft(opponentsGenerator(modelCache.getPlayersList()));
        //root.setLeft(otherPlayerGenerator());
        //root.setBottom(new VBox(playersGenerator(modelCache.getPlayersList())));
        //Island creation
        Group table = new Group();
        Image island;
        ImageView motherNature = new ImageView(new Image("Table/motherNature.png"));
        motherNature.setFitWidth(20);
        motherNature.setFitHeight(28);
        List<StackPane> islands = new ArrayList<>();
        int numberOfIslands = modelCache.getIslands().size();
        int motherNaturePosition = modelCache.getMotherNature();
        int centerX = 100;
        int centerY = 100;
        int radius = 300;
        for (int i = 0; i < numberOfIslands; i++) {
            switch (i % 3) {
                case 1:
                    island = new Image("island2.png");
                    break;
                case 2:
                    island = new Image("island3.png");
                    break;
                default:
                    island = new Image("new_island.png");
            }
            ImageView islandImageView = new ImageView(island);
            islandImageView.setFitWidth(134);
            islandImageView.setFitHeight(130);
            StackPane islandStack;
            if (i == motherNaturePosition) {
                islandStack = new StackPane(islandImageView, islandComponents(i, modelCache), motherNature);
            } else {
                islandStack = new StackPane(islandImageView, islandComponents(i, modelCache));
            }
            islandStack.relocate(centerX + radius * Math.cos(2 * Math.PI * i / numberOfIslands), centerY + radius * Math.sin(2 * Math.PI * i / numberOfIslands));
            islandButton(islandStack);
            islands.add(islandStack);
        }
        table.getChildren().addAll(islands);


        //Cloud creation
        Image cloud = new Image("cloud.png");
        List<StackPane> cloudView = new ArrayList<>();
        int cloudRadius = 100;
        for (int i = 0; i < numberOfPlayers; i++) {
            ImageView cloudImageView = new ImageView(cloud);
            cloudImageView.setFitHeight(140);
            cloudImageView.setFitWidth(140);
            StackPane cloudStack;
            cloudStack = new StackPane(cloudImageView, cloudComponents(i, modelCache));
            cloudStack.relocate(centerX + cloudRadius * Math.cos(2 * Math.PI * i / numberOfPlayers), centerY + cloudRadius * Math.sin(2 * Math.PI * i / numberOfPlayers));
            cloudButton(cloudStack);
            cloudView.add(cloudStack);
        }
        table.getChildren().addAll(cloudView);

        root.setCenter(table);

        setRight(modelCache);

        setTop(modelCache);
    }

    private Group cloudComponents(int j, ShowModelPayload modelCache) {
        Group ans = new Group();
        List<HBox> hboxComponents = new ArrayList<>();
        int radius = 40;
        int numOfComponents = 5;
        int i = 0;
        for (Creature c : Creature.values()) {
            int num = modelCache.getClouds().get(j).getStudents().stream().filter(s -> s.getCreature().equals(c)).toList().size();
            if (num > 0) {
                HBox creature = creatureCounter(c, num);
                creature.relocate(radius * Math.cos(2 * Math.PI * i / numOfComponents), radius * Math.sin(2 * Math.PI * i / numOfComponents));
                hboxComponents.add(creature);
                i++;
            }
        }
        ans.getChildren().addAll(hboxComponents);
        return ans;
    }

    private Group islandComponents(int j, ShowModelPayload modelCache) {
        Group ans = new Group();
        List<HBox> hboxComponents = new ArrayList<>();
        int radius = 50;
        int numOfComponents = 7;
        int i = 0;
        for (Creature c : Creature.values()) {
            int num = modelCache.getIslands().get(j).getNumberOfStudentsByCreature(c);
            if (num > 0) {
                HBox creature = creatureCounter(c, num);
                creature.relocate(radius * Math.cos(2 * Math.PI * i / numOfComponents), radius * Math.sin(2 * Math.PI * i / numOfComponents));
                hboxComponents.add(creature);
                i++;
            }

        }
        Text index = new Text("I:" + j);
        index.setFont(Font.font(20));
        HBox indexBox = new HBox(index);
        index.relocate(radius * Math.cos(2 * Math.PI * 6 / numOfComponents), radius * Math.sin(2 * Math.PI * 6 / numOfComponents));
        hboxComponents.add(indexBox);
        if (!isEmpty(modelCache.getIslands().get(j))) {
            HBox tower = towerCounter(modelCache.getIslands().get(j).getColorOfTowers(), modelCache.getIslands().get(j).getNumberOfTowers());
            tower.relocate(radius * Math.cos(2 * Math.PI * i / numOfComponents), radius * Math.sin(2 * Math.PI * i / numOfComponents));
            hboxComponents.add(tower);
        }
        ans.getChildren().addAll(hboxComponents);
        return ans;
    }

    private boolean isEmpty(Island i) {
        return i.getNumberOfTowers() == 0;
    }

    private void setRight(ShowModelPayload modelCache) {
        VBox players = new VBox(playersGenerator(modelCache.getPlayersList()));
        players.setAlignment(Pos.CENTER);

        Text creatureText = new Text("Creatures");

        List<HBox> creatureImages = new ArrayList<>();
        for (Creature c : Creature.values()) {
            ImageView creature = new ImageView(new Image(c.getImage()));
            creature.setFitWidth(100);
            creature.setFitHeight(100);
            HBox container = new HBox(creature);
            container.setOnMouseClicked(e -> {
                if (clientState.getHeaders().equals(Headers.action)) {
                    String creatureLetter;
                    switch (c) {
                        case PINK_FAIRIES:
                            creatureLetter = "P";
                            break;
                        case GREEN_FROGS:
                            creatureLetter = "G";
                            break;
                        case BLUE_UNICORNS:
                            creatureLetter = "B";
                            break;
                        case YELLOW_GNOMES:
                            creatureLetter = "Y";
                            break;
                        case RED_DRAGONS:
                            creatureLetter = "R";
                            break;
                        default:
                            creatureLetter = "";
                    }
                    guiEvents.add(creatureLetter);
                }
            });
            container.setStyle(noBorder);
            container.setOnMouseMoved(e -> {
                if (clientState.getHeaders().equals(Headers.action)) {
                    container.setStyle(borderSelected);
                }
            });
            container.setOnMouseExited(e -> {
                container.setStyle(noBorder);
            });
            creatureImages.add(container);
        }

        HBox creatures = new HBox();
        creatures.getChildren().addAll(creatureImages);
        creatures.setSpacing(5);
        creatures.setAlignment(Pos.CENTER);


        Text assistantsText = new Text("Available Assistants");

        List<HBox> assistantImages = new ArrayList<>();
        for (Assistants a : Assistants.values()) {
            ImageView assistant = new ImageView(new Image(a.getAssistant()));
            assistant.setFitWidth(100);
            assistant.setFitHeight(146);
            HBox container = new HBox(assistant);
            container.setStyle(borderUnselected);
            container.setOnMouseMoved(e -> {
                if (clientState.getHeaders().equals(Headers.planning)) {
                    container.setStyle(borderSelected);
                }
            });
            container.setOnMouseExited(e -> {
                container.setStyle(borderUnselected);
            });
            container.setOnMouseClicked(e -> {
                if (clientState.getHeaders().equals(Headers.planning)) {
                    guiEvents.add("" + a.ordinal());
                }
            });
            container.setOnMousePressed(e -> {
                container.setStyle("-fx-opacity: 0.3");
            });
            assistantImages.add(container);
        }


        HBox assistantsHigh = new HBox();
        assistantsHigh.getChildren().addAll(assistantImages.subList(0, assistantImages.size() / 2));
        assistantsHigh.setSpacing(5);
        assistantsHigh.setAlignment(Pos.CENTER);

        HBox assistantsLow = new HBox();
        assistantsLow.getChildren().addAll(assistantImages.subList(assistantImages.size() / 2, assistantImages.size()));
        assistantsLow.setSpacing(5);
        assistantsLow.setAlignment(Pos.CENTER);

        VBox assistants = new VBox(assistantsHigh, assistantsLow);
        assistants.setSpacing(5);
        assistants.setAlignment(Pos.CENTER);

        if (modelCache.isAdvancedRules()) {
            Text charsText = new Text("Available characters");
            ImageView character;
            List<HBox> characterImages = new ArrayList<>();
            for (CharacterInformation c : modelCache.getCharacters()) {
                character = new ImageView(new Image(c.getName().getImage()));
                character.setFitWidth(133);
                character.setFitHeight(200);
                character.setStyle(borderUnselected);
                HBox container = new HBox(character);
                container.setOnMouseClicked(e -> {
                    if (clientState.isSelectCharacter()) {
                        guiEvents.add("PC:" + c.getIndex());
                    }
                });
                container.setStyle(borderUnselected);
                container.setOnMouseMoved(e -> {
                    if (clientState.isSelectCharacter()) {
                        container.setStyle(borderSelected);
                    }
                });
                container.setOnMouseExited(e -> {
                    container.setStyle(borderUnselected);
                });
                characterImages.add(container);
            }

            HBox characters = new HBox();
            characters.getChildren().addAll(characterImages);
            characters.setSpacing(5);
            characters.setAlignment(Pos.CENTER);

            VBox interactiveAssets = new VBox(players, charsText, characters, creatureText, creatures, assistantsText, assistants);
            interactiveAssets.setSpacing(5);
            interactiveAssets.setAlignment(Pos.CENTER);
            root.setRight(interactiveAssets);
        } else {
            VBox interactiveAssets = new VBox(players, creatureText, creatures, assistantsText, assistants);
            interactiveAssets.setSpacing(5);
            interactiveAssets.setAlignment(Pos.CENTER);
            root.setRight(interactiveAssets);
        }

    }

    private void setTop(ShowModelPayload modelCache) {
        Button quit = new Button("Quit Game");
        quit.setFont(Font.font(30));
        quit.setOnAction(e -> quitStage());

        Text currentPhase = new Text();
        header = new SimpleStringProperty(clientState.getHeaders().toString());
        currentPhase.textProperty().bind(header);
        currentPhase.setStyle("-fx-border-insets: 5;");
        Text currentPrePhase = new Text("The current game phase is: ");
        HBox currentPhaseBox = new HBox(currentPrePhase, currentPhase);
        currentPhaseBox.setAlignment(Pos.CENTER);

        Text actionPhase = new Text();
        String text = "";
        if (clientState.isMoveStudents()) {
            text += "Move the students";
        } else if (clientState.isMoveMotherNature()) {
            text += "Move Mother Nature";
        } else if (clientState.isSelectCloud()) {
            text += "Select a cloud";
        }
        if (clientState.isSelectCharacter()) {
            text += " or Select a Character";
        }
        actionPhase.setText(text);
        HBox actionPhaseBox = new HBox(actionPhase);
        actionPhaseBox.setAlignment(Pos.CENTER);

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

        HBox topMenu;
        if (modelCache.isAdvancedRules()) {
            HBox bank = createBankComponent(modelCache.getCoinReserve());
            topMenu = new HBox(currentPlayerBox, actionPhaseBox, currentPhaseBox, bank, quit);
        } else {
            topMenu = new HBox(currentPlayerBox, actionPhaseBox, currentPhaseBox, quit);
        }
        topMenu.setAlignment(Pos.TOP_RIGHT);
        topMenu.setSpacing(50);
        root.setTop(topMenu);
    }

    private HBox createBankComponent(int i) {
        Text title = new Text("Coin Reserve:");
        ImageView coin = new ImageView("moneta.png");
        coin.setFitWidth(50);
        coin.setFitHeight(50);
        Text num = new Text("" + i + "");
        num.setFont(Font.font(30));
        HBox coins = new HBox(num, coin);
        coins.setSpacing(10);
        coins.setAlignment(Pos.CENTER);
        HBox ans = new HBox(title, coins);
        ans.setAlignment(Pos.CENTER);
        ans.setSpacing(5);
        return ans;
    }

    private VBox opponentsGenerator(List<Player> players) {
        List<VBox> generatedPlayers = new ArrayList<>();
        VBox ans;
        for (Player p : players) {
            if (!p.getUsername().equals(MY_USERNAME)) {
                generatedPlayers.add(otherPlayerGenerator(p));
            }
        }
        ans = new VBox();
        ans.getChildren().addAll(generatedPlayers);
        ans.setSpacing(5);
        return ans;
    }

    private VBox otherPlayerGenerator(Player p) {
        Text username = new Text(p.getUsername());
        username.setFont(Font.font(20));
        ImageView wizard = new ImageView(new Image(p.getWizard().getImage()));
        wizard.setFitHeight(50);
        wizard.setFitWidth(33);
        HBox playerInfo = new HBox(wizard, username);
        playerInfo.setAlignment(Pos.CENTER);
        playerInfo.setSpacing(10);
        VBox player = new VBox(playerInfo,
                createComponentWithCreatures("Entrance", p),
                createComponentWithCreatures("Dining Room", p),
                createComponentWithCreatures("Professors", p),
                createTowerComponent(p));
        player.setSpacing(5);
        player.setAlignment(Pos.CENTER);
        player.setStyle(cssLayout);
        return player;
    }

    private VBox playersGenerator(List<Player> players) {
        VBox playerList = new VBox();
        for (Player p : players) {
            Text username = new Text(p.getUsername());
            ImageView wizard = new ImageView(new Image(p.getWizard().getImage()));
            wizard.setFitHeight(33);
            wizard.setFitWidth(33);
            StackPane wizardStack = new StackPane();
            wizardStack.getChildren().add(new Circle(25, new ImagePattern(new Image(p.getWizard().getImage()))));
            HBox playerInfo = new HBox(wizardStack, username);
            playerInfo.setSpacing(10);
            playerInfo.setAlignment(Pos.CENTER);
            HBox player = new HBox(playerInfo,
                    createComponentWithAssistant(p.getLastPlayedCards()),
                    createComponentWithCreatures("Entrance", p),
                    createComponentWithCreatures("Dining Room", p),
                    createComponentWithCreatures("Professors", p),
                    createTowerComponent(p));
            player.setSpacing(5);
            player.setAlignment(Pos.CENTER);
            playerList.getChildren().add(player);
        }
        return playerList;
    }

    private VBox createComponentWithAssistant(List<Assistant> lastPlayedAssistants) {
        Text assistantTitle = new Text("Assistant");
        HBox assistant;
        StackPane assistantStack = new StackPane();
        if (lastPlayedAssistants.size() > 0) {
            assistantStack.getChildren().add(new Circle(25, new ImagePattern(new Image(lastPlayedAssistants.get(lastPlayedAssistants.size() - 1).getName().getAssistant()))));
        } else {
            assistantStack.getChildren().add(new Circle(25, new ImagePattern(new Image("strawberry.png"))));
        }
        assistant = new HBox(assistantStack);
        assistant.setAlignment(Pos.CENTER);
        VBox assistantBox = new VBox(assistantTitle, assistant);
        assistantBox.setAlignment(Pos.CENTER);
        return assistantBox;
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

    private HBox professorsCounter(Creature creature) {
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
        creatureImage.setFitWidth(20);
        creatureImage.setFitHeight(20);
        HBox ans = new HBox(creatureImage);
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

    private VBox createComponentWithCreatures(String name, Player p) {
        Text title = new Text(name);
        VBox ans = new VBox(title, createCreatures(name, p));
        ans.setAlignment(Pos.CENTER);
        ans.setSpacing(5);
        ans.setStyle(cssLayout);
        return ans;
    }

    private HBox createCreatures(String name, Player p) {
        HBox ans = new HBox();
        List<HBox> creatureCounters = new ArrayList<>();
        switch (name) {
            case "Entrance":
                for (Creature c : Creature.values()) {
                    creatureCounters.add(creatureCounter(c, p.getEntrance().getNumberOfStudentsByCreature(c)));
                }
                break;
            case "Dining Room":
                for (Creature c : Creature.values()) {
                    creatureCounters.add(creatureCounter(c, p.getDiningRoom().getNumberOfStudentsByCreature(c)));
                }
                break;
            case "Professors":
                for (Creature c : Creature.values()) {
                    if (p.hasProfessor(c)) {
                        creatureCounters.add(professorsCounter(c));
                    }
                }
                break;

        }
        ans.getChildren().addAll(creatureCounters);

        ans.setAlignment(Pos.CENTER);
        ans.setSpacing(5);
        return ans;
    }


    private VBox createTowerComponent(Player p) {
        HBox towers = towerCounter(p.getMyColor(), p.getTowers());
        towers.setSpacing(10);
        towers.setAlignment(Pos.CENTER);
        VBox ans = new VBox(towers);
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

    public void updateClientState(ClientState cs) {
        clientState = cs;
    }

    private void islandButton(Pane object) {
        object.setOnMouseMoved(e -> {
            if (clientState.isMoveMotherNature() || clientState.isMoveStudents()) {
                object.setStyle(borderSelected);
            }
        });
        object.setOnMouseExited(e -> {
            object.setStyle(noBorder);
        });
    }

    private void cloudButton(Pane object) {
        object.setOnMouseMoved(e -> {
            if (clientState.isSelectCloud()) {
                object.setStyle(borderSelected);
            }
        });
        object.setOnMouseExited(e -> {
            object.setStyle(noBorder);
        });
    }
}



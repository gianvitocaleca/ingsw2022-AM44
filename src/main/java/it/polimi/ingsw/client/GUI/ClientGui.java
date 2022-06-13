package it.polimi.ingsw.client.GUI;

import it.polimi.ingsw.client.ClientState;
import it.polimi.ingsw.client.sender.AbstractSender;
import it.polimi.ingsw.client.sender.ConcreteGUISender;
import it.polimi.ingsw.server.CharacterInformation;
import it.polimi.ingsw.server.model.enums.Creature;
import it.polimi.ingsw.server.model.player.Assistant;
import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.server.model.studentcontainers.Island;
import it.polimi.ingsw.server.networkMessages.Headers;
import it.polimi.ingsw.server.networkMessages.payloads.ShowModelPayload;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;

import static it.polimi.ingsw.client.GUI.GuiAssets.*;
import static it.polimi.ingsw.client.GUI.GuiCss.*;

public class ClientGui extends Application {
    private static String address;
    private static int port;
    private int numberOfPlayers = 2;
    private Stage stage;
    private BorderPane root;
    private AbstractSender client;
    private String MY_USERNAME;
    private Queue<String> guiEvents;
    private ClientState clientState;
    private GUIPhases guiPhases = GUIPhases.SELECT_CREATURE;
    private String createdCommand = "";
    private ShowModelPayload modelCache;

    /**
     * @param address is the address to be set for the client
     */
    public static void setAddress(String address) {
        ClientGui.address = address;
    }

    /**
     * @param port is the port to be set for the client
     */
    public static void setPort(int port) {
        ClientGui.port = port;
    }

    /**
     * Called by the startup process of javaFx
     * Creates the Pane for the Scene
     * Populates the Pane with starting info
     * Determines Stage appearance
     */
    @Override
    public void start(Stage primaryStage) throws IOException {

        stage = primaryStage;

        //Pane creation
        root = new BorderPane();
        root.setStyle(bodyFont);

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

    /**
     * Creates the button to join the game
     *
     * @return
     */
    private Button joinButton() {
        //Background
        root.setBackground(new Background(new BackgroundImage(new Image("sfondoCreazione.png"), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
        Button join = new Button(joinButton);
        join.setStyle(headerFont);
        join.setOnAction(e -> {
            startSender();
        });
        return join;
    }

    /**
     * Starts the message sender on a separate thread
     */
    private void startSender() {
        guiEvents = new LinkedList<>();
        client = new ConcreteGUISender(address, port, this, guiEvents);
        Thread senderThread = new Thread(() -> {
            try {
                client.startClient();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        senderThread.start();
    }

    /**
     * Creates the title of the game
     *
     * @return is the Box containing the title
     */
    private VBox gameTitle() {
        Text title = new Text(gameTitle);
        title.setFont(titleFont);
        title.setFill(Color.DARKVIOLET);
        title.setStroke(Color.LIGHTGOLDENRODYELLOW);
        title.setStrokeWidth(1);
        VBox titleBox = new VBox(title);
        titleBox.setAlignment(Pos.CENTER);
        return titleBox;
    }

    /**
     * Creates and sets the buttons for number of player selection
     */
    public void numberOfPlayers() {
        Button twoPlayers = new Button(twoPlayersButton);
        Button threePlayers = new Button(threePlayersButton);
        HBox numOfPlayersButtons = new HBox(twoPlayers, threePlayers);
        numOfPlayersButtons.setStyle(headerFont);
        numOfPlayersButtons.setSpacing(smallSpacing);
        numOfPlayersButtons.setAlignment(Pos.CENTER);

        twoPlayers.setOnAction(e -> {
            this.numberOfPlayers = 2;
            guiEvents.add(String.valueOf(this.numberOfPlayers));
        });
        threePlayers.setOnAction(e -> {
            this.numberOfPlayers = 3;
            guiEvents.add(String.valueOf(this.numberOfPlayers));
        });
        root.setCenter(numOfPlayersButtons);
    }

    /**
     * Creates and sets the buttons for type of rules selection
     */
    public void typeOfRules() {
        Button basicRules = new Button(basicRulesButton);
        Button advancedRules = new Button(advancedRulesButton);
        HBox typeOfRulesButtons = new HBox(basicRules, advancedRules);
        typeOfRulesButtons.setStyle(headerFont);
        typeOfRulesButtons.setSpacing(smallSpacing);
        typeOfRulesButtons.setAlignment(Pos.CENTER);
        basicRules.setOnAction(e -> {
            guiEvents.add(basicRulesCode);
        });
        advancedRules.setOnAction(e -> {
            guiEvents.add(advancedRulesCode);
        });
        root.setCenter(typeOfRulesButtons);
    }

    /**
     * Creates and sets the Text field to insert the player's username
     */
    public void loginUsername() {
        Text text = new Text(provideUsernameText);
        text.setStyle(bodyFont);
        TextField prompt = new TextField("");
        prompt.setMaxWidth(200);
        prompt.setOnAction(e -> {
            guiEvents.add(prompt.getCharacters().toString());
            MY_USERNAME = prompt.getCharacters().toString().toLowerCase();
        });
        VBox box = new VBox(text, prompt);
        box.setSpacing(smallSpacing);
        box.setAlignment(Pos.CENTER);
        root.setCenter(box);
    }

    /**
     * Creates the Choice Box to select the player's color
     */
    public void color() {
        Text text = new Text("Choose a color");
        text.setStyle(bodyFont);
        ChoiceBox<String> prompt = new ChoiceBox<>();
        prompt.getItems().addAll(whiteColorText, blackColorText, greyColorText);
        Button button = new Button();
        button.setText("Confirm");
        button.setOnAction(e -> {
            switch (prompt.getValue()) {
                case whiteColorText:
                    guiEvents.add(whiteColorCode);
                    break;
                case blackColorText:
                    guiEvents.add(blackColorCode);
                    break;
                case greyColorText:
                    guiEvents.add(greyColorCode);
                    break;
            }
        });
        choiceBoxesHBoxCreator(text, prompt, button);
    }

    /**
     * Creates the Choice Box to select the player's wizard
     */
    public void wizard() {
        Text text = new Text("Pick a wizard");
        text.setStyle(bodyFont);
        ChoiceBox<String> prompt = new ChoiceBox<>();
        prompt.getItems().addAll(firstWizardText, secondWizardText, thirdWizardText, fourthWizardText);
        Button button = new Button();
        button.setText("Confirm");
        button.setOnAction(e -> {
            switch (prompt.getValue()) {
                case firstWizardText:
                    guiEvents.add(firstWizardCode);
                    break;
                case secondWizardText:
                    guiEvents.add(secondWizardCode);
                    break;
                case thirdWizardText:
                    guiEvents.add(thirdWizardCode);
                    break;
                case fourthWizardText:
                    guiEvents.add(fourthWizardCode);
                    break;
            }
        });
        choiceBoxesHBoxCreator(text, prompt, button);
    }

    /**
     * Creates and sets a Box in the center of the Pane
     *
     * @param text   header
     * @param prompt Choice Box to be used
     * @param button to confirm Selection
     */
    private void choiceBoxesHBoxCreator(Text text, ChoiceBox<String> prompt, Button button) {
        HBox hBox = new HBox(prompt, button);
        hBox.setSpacing(mediumSpacing);
        hBox.setAlignment(Pos.CENTER);
        VBox box = new VBox(text, hBox);
        box.setSpacing(smallSpacing);
        box.setAlignment(Pos.CENTER);

        root.setCenter(box);
    }

    /**
     * Alert to show errors to the player
     *
     * @param string
     */
    public void errorAlert(String string) {
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
    public void winnerAlert(String string) {
        Alert a = new Alert(Alert.AlertType.INFORMATION,
                string,
                ButtonType.OK);
        a.setTitle(gameTitle);
        a.setHeaderText("Winner");
        a.showAndWait();
    }

    /**
     * Creates and sets the Pane during the game
     *
     * @param modelCache
     */
    public void gamePaneGenerator(ShowModelPayload modelCache) {
        this.modelCache = modelCache;
        root.setBackground(new Background(new BackgroundFill(backgroundColor, CornerRadii.EMPTY, Insets.EMPTY)));
        Group table = new Group();

        table.getChildren().addAll(islandsGenerator());

        table.getChildren().addAll(cloudsGenerator());

        root.setCenter(table);

        setRight();

        setTop();
    }

    /**
     * Creates the circle of islands
     *
     * @return the list of islands
     */
    private List<StackPane> islandsGenerator() {
        Image island;
        ImageView motherNature = new ImageView(new Image("Table/motherNature.png"));
        motherNature.setFitWidth(motherNatureWidth);
        motherNature.setFitHeight(motherNatureHeight);
        List<StackPane> islands = new ArrayList<>();
        int numberOfIslands = modelCache.getIslands().size();
        int motherNaturePosition = modelCache.getMotherNature();
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
            islandImageView.setFitWidth(islandWidth);
            islandImageView.setFitHeight(islandHeight);
            StackPane islandStack;
            if (i == motherNaturePosition) {
                islandStack = new StackPane(islandImageView, islandComponents(i), motherNature);
            } else {
                islandStack = new StackPane(islandImageView, islandComponents(i));
            }
            islandStack.relocate(
                    getCoordinatesX(centerX, islandRadius, i, numberOfIslands),
                    getCoordinatesY(centerY, islandRadius, i, numberOfIslands));
            islandButton(islandStack, i);
            islands.add(islandStack);
        }
        return islands;
    }

    /**
     * Creates the circle of clouds
     *
     * @return the list of clouds
     */
    private List<StackPane> cloudsGenerator() {
        Image cloud = new Image("cloud.png");
        List<StackPane> clouds = new ArrayList<>();
        for (int i = 0; i < numberOfPlayers; i++) {
            ImageView cloudImageView = new ImageView(cloud);
            cloudImageView.setFitHeight(cloudHeight);
            cloudImageView.setFitWidth(cloudWidth);
            StackPane cloudStack;
            cloudStack = new StackPane(cloudImageView, cloudComponents(i));
            cloudStack.relocate(
                    getCoordinatesX(centerX, cloudRadius, i, numberOfPlayers),
                    getCoordinatesX(centerY, cloudRadius, i, numberOfPlayers));
            cloudButton(cloudStack, i);
            clouds.add(cloudStack);
        }
        return clouds;
    }

    /**
     * @param center       the X coordinate of the center of the polygon
     * @param radius       of the polygon
     * @param index        the vertex position on the regular polygon
     * @param totalIndexes the number of total vertices in the polygon
     * @return the X coordinate of a vertex in the regular polygon
     */
    private double getCoordinatesX(int center, int radius, int index, int totalIndexes) {
        return center + radius * Math.cos(2 * Math.PI * index / totalIndexes);
    }

    /**
     * @param center       the Y coordinate of the center of the polygon
     * @param radius       of the polygon
     * @param index        the vertex position on the regular polygon
     * @param totalIndexes the number of total vertices in the polygon
     * @return the Y coordinate of a vertex in the regular polygon
     */
    private double getCoordinatesY(int center, int radius, int index, int totalIndexes) {
        return center + radius * Math.sin(2 * Math.PI * index / totalIndexes);
    }

    /**
     * @param j is the index of the cloud
     * @return is the circle of components in a cloud
     */
    private Group cloudComponents(int j) {
        Group ans = new Group();
        List<HBox> hBoxComponents = new ArrayList<>();
        int i = 0;
        for (Creature c : Creature.values()) {
            int num = modelCache.getClouds().get(j).getStudents().stream().filter(s -> s.getCreature().equals(c)).toList().size();
            i = componentsDisposition(hBoxComponents, cloudContentRadius, cloudContentComponents, i, c, num);
        }
        ans.getChildren().addAll(hBoxComponents);
        return ans;
    }

    /**
     * @param j is the index of the island
     * @return is the circle of components in an island
     */
    private Group islandComponents(int j) {
        Group ans = new Group();
        List<HBox> hBoxComponents = new ArrayList<>();
        int i = 0;
        for (Creature c : Creature.values()) {
            int num = modelCache.getIslands().get(j).getNumberOfStudentsByCreature(c);
            i = componentsDisposition(hBoxComponents, islandContentRadius, islandContentComponents, i, c, num);

        }
        if (!hasNoTowers(modelCache.getIslands().get(j))) {
            HBox tower = towerCounter(modelCache.getIslands().get(j).getColorOfTowers(), modelCache.getIslands().get(j).getNumberOfTowers());
            if (tower != null) {

                tower.relocate(
                        getCoordinatesX(islandContentCenter, islandContentRadius, i, islandContentComponents),
                        getCoordinatesY(islandContentCenter, islandContentRadius, i, islandContentComponents));
            }
            hBoxComponents.add(tower);
        }
        ans.getChildren().addAll(hBoxComponents);
        return ans;
    }

    /**
     * @param hBoxComponents  is the list of boxes to populate with content
     * @param radius
     * @param numOfComponents
     * @param i
     * @param c
     * @param num
     * @return is the next component index
     */
    private int componentsDisposition(List<HBox> hBoxComponents, int radius, int numOfComponents, int i, Creature c, int num) {
        if (num > 0) {
            HBox creature = creatureCounter(c, num);
            if (creature != null) {
                creature.relocate(
                        getCoordinatesX(0, radius, i, numOfComponents),
                        getCoordinatesY(0, radius, i, numOfComponents));
            }
            hBoxComponents.add(creature);
            i++;
        }
        return i;
    }

    /**
     * Used to verify if an island has no towers
     *
     * @param i the island
     * @return
     */
    private boolean hasNoTowers(Island i) {
        return i.getNumberOfTowers() == 0;
    }

    /**
     * Sets the creatures, the assistants and the characters on the right of the pane
     */
    private void setRight() {
        VBox players = new VBox(playersGenerator(modelCache.getPlayersList()));
        players.setAlignment(Pos.CENTER);

        Text creatureText = new Text("Creatures");

        HBox creatures = createCreaturesComponent();

        Text assistantsText = new Text("Available Assistants");

        VBox assistants = assistantsGenerator();

        VBox interactiveAssets;

        if (modelCache.isAdvancedRules()) {
            Text charsText = new Text("Available characters");

            HBox characters = charactersGenerator();

            interactiveAssets = new VBox(players, charsText, characters, creatureText, creatures, assistantsText, assistants);

        } else {
            interactiveAssets = new VBox(players, creatureText, creatures, assistantsText, assistants);
        }
        interactiveAssets.setSpacing(smallSpacing);
        interactiveAssets.setAlignment(Pos.CENTER);
        root.setRight(interactiveAssets);
    }

    /**
     * @return is the list of remaining assistants of the player
     */
    private VBox assistantsGenerator() {
        List<HBox> assistantImages = new ArrayList<>();

        Optional<Player> myOptionalPlayer = modelCache.getPlayersList().stream().filter(p -> p.getUsername().equals(MY_USERNAME)).findFirst();
        Player myPlayer;
        if (myOptionalPlayer.isPresent()) {
            myPlayer = myOptionalPlayer.get();

            for (Assistant a : myPlayer.getAssistantDeck()) {
                ImageView assistant = new ImageView(new Image(a.getName().getAssistant()));
                assistant.setFitWidth(assistantWidth);
                assistant.setFitHeight(assistantHeight);
                HBox container = new HBox(assistant);
                container.setStyle(borderUnselected);
                container.setOnMouseMoved(e -> {
                    if (clientState.getHeaders().equals(Headers.planning) && (modelCache.getCurrentPlayerUsername().equals(MY_USERNAME))) {
                        container.setStyle(borderSelected);
                    }
                });
                container.setOnMouseExited(e -> {
                    container.setStyle(borderUnselected);
                });
                container.setOnMouseClicked(e -> {
                    if (clientState.getHeaders().equals(Headers.planning) && (modelCache.getCurrentPlayerUsername().equals(MY_USERNAME))) {
                        for (int i = 0; i < myPlayer.getAssistantDeck().size(); i++) {
                            if (a.getName().equals(myPlayer.getAssistantDeck().get(i).getName()))
                                guiEvents.add("" + i);
                        }

                    }
                });
                container.setOnMousePressed(e -> {
                    if (modelCache.getCurrentPlayerUsername().equals(MY_USERNAME)) {
                        container.setStyle(lowOpacity);
                    }
                });
                assistantImages.add(container);
            }
        }

        HBox assistantsHigh = new HBox();
        assistantsHigh.getChildren().addAll(assistantImages.subList(0, assistantImages.size() / 2));
        assistantsHigh.setSpacing(smallSpacing);
        assistantsHigh.setAlignment(Pos.CENTER);

        HBox assistantsLow = new HBox();
        assistantsLow.getChildren().addAll(assistantImages.subList(assistantImages.size() / 2, assistantImages.size()));
        assistantsLow.setSpacing(smallSpacing);
        assistantsLow.setAlignment(Pos.CENTER);

        VBox assistants = new VBox(assistantsHigh, assistantsLow);
        assistants.setSpacing(smallSpacing);
        assistants.setAlignment(Pos.CENTER);

        return assistants;
    }

    /**
     * @return is the list of characters available to the player
     */
    private HBox charactersGenerator() {
        ImageView character;
        List<HBox> characterImages = new ArrayList<>();
        for (CharacterInformation c : modelCache.getCharacters()) {
            character = new ImageView(new Image(c.getName().getImage()));
            character.setFitWidth(characterWidth);
            character.setFitHeight(characterHeight);
            character.setStyle(borderUnselected);
            HBox container = new HBox(character);
            container.setOnMouseClicked(e -> {
                if (clientState.isSelectCharacter()) {
                    guiEvents.add(playCharacterCode + c.getIndex());
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
        characters.setSpacing(smallSpacing);
        characters.setAlignment(Pos.CENTER);
        return characters;
    }

    /**
     * @param c is the given creature
     * @return is the corresponding string
     */
    private String creatureCode(Creature c) {
        String creatureLetter = "";
        if (guiPhases == GUIPhases.SELECT_CREATURE) {
            switch (c) {
                case PINK_FAIRIES:
                    creatureLetter = pinkCreatureText;
                    break;
                case GREEN_FROGS:
                    creatureLetter = greenCreatureText;
                    break;
                case BLUE_UNICORNS:
                    creatureLetter = blueCreatureText;
                    break;
                case YELLOW_GNOMES:
                    creatureLetter = yellowCreatureText;
                    break;
                case RED_DRAGONS:
                    creatureLetter = redCreatureText;
                    break;
            }
        }

        return creatureLetter;
    }

    /**
     * @return is the list of creatures used by the player to make actions
     */
    private HBox createCreaturesComponent() {
        List<HBox> creatureImages = new ArrayList<>();
        for (Creature c : Creature.values()) {
            ImageView creature = new ImageView(new Image(c.getImage()));
            creature.setFitWidth(creatureWidth);
            creature.setFitHeight(creatureHeight);
            HBox container = new HBox(creature);
            container.setOnMouseClicked(e -> {
                if (guiPhases == GUIPhases.SELECT_CREATURE) {
                    createdCommand = moveStudentsCode;
                    createdCommand += creatureCode(c);
                    guiPhases = GUIPhases.SELECT_DESTINATION;
                } else if (guiPhases == GUIPhases.SELECT_CREATURE_FOR_CHARACTER) {
                    createdCommand = selectCreatureCode;
                    createdCommand += creatureCode(c);
                    guiPhases = GUIPhases.SELECT_DESTINATION_ISLAND;
                }

            });
            container.setStyle(noBorder);
            container.setOnMouseMoved(e -> {
                if (clientState.getHeaders().equals(Headers.action) && (clientState.getModelPayload().getCurrentPlayerUsername().equals(MY_USERNAME))) {
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
        creatures.setSpacing(smallSpacing);
        creatures.setAlignment(Pos.CENTER);
        return creatures;
    }

    /**
     * Sets the quit button, current phase and player, the hint for the action phase and the bank on the top of the pane
     */
    private void setTop() {
        Button quit = new Button("Quit Game");
        quit.setStyle(headerFont);
        quit.setOnAction(e -> quitStage());

        HBox currentPhaseBox = createCurrentPhaseBox();

        HBox actionPhaseBox = createActionPhaseBox();

        HBox currentPlayerBox = createCurrentPlayerBox();

        HBox topMenu;
        if (modelCache.isAdvancedRules()) {
            HBox bank = createBankComponent();
            topMenu = new HBox(currentPlayerBox, actionPhaseBox, currentPhaseBox, bank, quit);
        } else {
            topMenu = new HBox(currentPlayerBox, actionPhaseBox, currentPhaseBox, quit);
        }
        topMenu.setAlignment(Pos.TOP_RIGHT);
        topMenu.setSpacing(50);
        root.setTop(topMenu);
    }

    /**
     * @return is the current phase container box
     */
    private HBox createCurrentPhaseBox() {
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
     * @return is the action phase container box
     */
    private HBox createActionPhaseBox() {
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
     * @return is the current player container box
     */
    private HBox createCurrentPlayerBox() {
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
     * @return is the bank container box
     */
    private HBox createBankComponent() {
        Text title = new Text("Coin Reserve:");
        ImageView coin = new ImageView("moneta.png");
        coin.setFitWidth(coinWidth);
        coin.setFitHeight(coinHeight);
        Text num = new Text("" + modelCache.getCoinReserve() + "");
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
     * Populates the player boxes with titles and components
     *
     * @param players is the list of players
     * @return is the player's container box
     */
    private VBox playersGenerator(List<Player> players) {
        VBox playerList = new VBox();
        for (Player p : players) {
            Text username = new Text(p.getUsername());
            ImageView wizard = new ImageView(new Image(p.getWizard().getImage()));
            wizard.setFitHeight(wizardHeight);
            wizard.setFitWidth(wizardWidth);
            StackPane wizardStack = new StackPane();
            wizardStack.getChildren().add(new Circle(playerRadius, new ImagePattern(new Image(p.getWizard().getImage()))));
            HBox playerInfo = new HBox(wizardStack, username);
            playerInfo.setSpacing(mediumSpacing);
            playerInfo.setAlignment(Pos.CENTER);
            HBox player = new HBox(playerInfo,
                    createComponentWithAssistant(p.getLastPlayedCards()),
                    createComponentWithCreatures(entranceHeaderText, p),
                    createComponentWithCreatures(diningRoomHeaderText, p),
                    createComponentWithCreatures(professorsHeaderText, p),
                    createTowerComponent(p));
            player.setSpacing(smallSpacing);
            player.setAlignment(Pos.CENTER);
            playerList.getChildren().add(player);
        }
        return playerList;
    }

    /**
     * @param lastPlayedAssistants is the content
     * @return is the assistant component
     */
    private VBox createComponentWithAssistant(List<Assistant> lastPlayedAssistants) {
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
     * @param creature
     * @param num      is the number of given creatures
     * @return is the container of the creature's image and text number
     */
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
        creatureImage.setFitWidth(playerContentWidth);
        creatureImage.setFitHeight(playerContentHeight);
        return counterText(num, creatureImage);
    }

    /**
     * @param creature is the professor's creature
     * @return the professor's container
     */
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
        creatureImage.setFitWidth(playerContentWidth);
        creatureImage.setFitHeight(playerContentHeight);
        HBox ans = new HBox(creatureImage);
        ans.setSpacing(smallSpacing);
        ans.setAlignment(Pos.CENTER);
        return ans;
    }

    /**
     * @param color
     * @param num   is the number of given towers
     * @return is the tower's container and text number
     */
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
        towerImage.setFitWidth(playerContentWidth);
        towerImage.setFitHeight(playerContentHeight * 2);
        return counterText(num, towerImage);
    }

    /**
     * @param num   is the text number to be shown
     * @param image is the image to be shown
     * @return is the box of the text and the image
     */
    private HBox counterText(int num, ImageView image) {
        Text numText = new Text("" + num + "");
        numText.setStyle(bodyFont);
        HBox ans = new HBox(image, numText);
        ans.setSpacing(smallSpacing);
        ans.setAlignment(Pos.CENTER);
        return ans;
    }

    /**
     * @param name is the header of the player's component
     * @param p    is the given player
     * @return is the creature and header container
     */
    private VBox createComponentWithCreatures(String name, Player p) {
        Text title = new Text(name);
        VBox ans = new VBox(title, createCreatures(name, p));
        ans.setAlignment(Pos.CENTER);
        ans.setSpacing(smallSpacing);
        ans.setStyle(defaultComponentLayout);
        if (name.equals(diningRoomHeaderText)) {
            if (p.getUsername().equals(MY_USERNAME)) {
                ans.setOnMouseMoved(e -> {
                    ans.setStyle(borderSelected);
                });
                ans.setOnMouseClicked(e -> {
                    if (guiPhases == GUIPhases.SELECT_DESTINATION) {
                        createdCommand += commandSeparator;
                        createdCommand += String.valueOf(0);
                        guiEvents.add(createdCommand);
                        createdCommand = "";
                    }
                });
                ans.setOnMouseExited(e -> {
                    ans.setStyle(defaultComponentLayout);
                });
            }
        }
        return ans;
    }

    /**
     * @return is the correct creature container
     */
    private HBox createCreatures(String name, Player p) {
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
     * @param p is the given player
     * @return is the tower and header container
     */
    private VBox createTowerComponent(Player p) {
        HBox towers = towerCounter(p.getMyColor(), p.getTowers());
        if (towers != null) {
            towers.setSpacing(mediumSpacing);
            towers.setAlignment(Pos.CENTER);
        }
        VBox ans = new VBox(towers);
        ans.setAlignment(Pos.CENTER);
        ans.setSpacing(smallSpacing);
        return ans;
    }

    /**
     * Closes the stage, sends the quit message and stops the execution
     */
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

    /**
     * @param cs is the new clientState to be set
     */
    public void updateClientState(ClientState cs) {
        clientState = cs;
    }

    /**
     * Creates a button like behaviour for the given island
     *
     * @param object is the island
     * @param i      is the island index
     */
    private void islandButton(Pane object, int i) {
        object.setOnMouseMoved(e -> {
            if (clientState.isMoveMotherNature() || guiPhases == GUIPhases.SELECT_DESTINATION) {
                object.setStyle(borderSelected);
            }
        });
        object.setOnMouseExited(e -> {
            object.setStyle(noBorder);
        });
        object.setOnMouseClicked(e -> {
            if (guiPhases == GUIPhases.SELECT_DESTINATION) {
                createdCommand += commandSeparator;
                createdCommand += String.valueOf(i + 1);
                guiEvents.add(createdCommand);
                createdCommand = "";
                guiPhases = GUIPhases.END;
            } else if (clientState.isMoveMotherNature()) {
                int mnPosition = clientState.getModelPayload().getMotherNature();
                createdCommand += moveMotherNatureCode;
                createdCommand += String.valueOf(evaluateMnJumps(mnPosition, i));
                guiEvents.add(createdCommand);
                createdCommand = "";
            } else if (guiPhases == GUIPhases.SELECT_DESTINATION_ISLAND) {
                createdCommand += selectDestinationIslandCode;
                createdCommand += String.valueOf(i + 1);
                guiEvents.add(createdCommand);
                createdCommand = "";
            }
        });
    }

    /**
     * @param islandIndex is the index of the island selected by the player
     * @return is the correct number of jumps for MN to make
     */
    private int evaluateMnJumps(int mnPosition, int islandIndex) {
        int jumps;
        if (mnPosition < islandIndex) {
            jumps = islandIndex - mnPosition;
        } else {
            jumps = clientState.getModelPayload().getIslands().size() - mnPosition;
            jumps += islandIndex;
        }
        return jumps;
    }

    /**
     * Creates a button like behaviour for the given cloud
     *
     * @param object is the cloud
     * @param i      is the cloud index
     */
    private void cloudButton(Pane object, int i) {
        object.setOnMouseMoved(e -> {
            if (clientState.isSelectCloud()) {
                object.setStyle(borderSelected);
            }
        });
        object.setOnMouseExited(e -> {
            object.setStyle(noBorder);
        });
        object.setOnMouseClicked(e -> {
            if (clientState.isSelectCloud()) {
                createdCommand += selectCloudCode;
                createdCommand += String.valueOf(i);
                guiEvents.add(createdCommand);
                createdCommand = "";
            }
        });
    }

    /**
     * Sets the gui phase to allow for students selection
     */
    public void setMoveStudents() {
        if (clientState.isMoveStudents()) {
            guiPhases = GUIPhases.SELECT_CREATURE;
        }
    }

    /**
     * Creates the alert for the characters that need to select creatures and destination
     */
    public void characterNeedsSourceCreaturesAndDestination() {
        String string = "Select a creature and then an island";
        guiPhases = GUIPhases.SELECT_CREATURE_FOR_CHARACTER;
        Alert a = new Alert(Alert.AlertType.INFORMATION,
                string,
                ButtonType.OK);
        a.setTitle(gameTitle);
        a.setHeaderText("Use Effect of : " + clientState.getCurrentPlayedCharacter());
        a.showAndWait();
    }
}



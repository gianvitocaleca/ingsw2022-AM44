package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.view.ClientState;
import it.polimi.ingsw.network.client.sender.AbstractSender;
import it.polimi.ingsw.network.client.sender.ConcreteGUISender;
import it.polimi.ingsw.network.server.CharacterInformation;
import it.polimi.ingsw.model.enums.Creature;
import it.polimi.ingsw.model.enums.Name;
import it.polimi.ingsw.model.player.Assistant;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.network.server.networkMessages.Headers;
import it.polimi.ingsw.network.server.networkMessages.payloads.ShowModelPayload;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;

import static it.polimi.ingsw.utils.Commands.*;
import static it.polimi.ingsw.utils.TextAssets.*;
import static it.polimi.ingsw.view.GUI.GuiAssets.*;
import static it.polimi.ingsw.view.GUI.GuiComponents.*;
import static it.polimi.ingsw.view.GUI.GuiCss.*;
import static it.polimi.ingsw.view.GUI.GuiAlerts.*;
import static it.polimi.ingsw.view.GUI.GuiMethods.*;

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
    private int creaturesToSwap = 1;
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
            quitStage(stage);
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
     * @param username is the username to be set
     */
    public void setMY_USERNAME(String username) {
        MY_USERNAME = username;
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
        button.setText(confirmText);
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
                default:
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
        button.setText(confirmText);
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
                default:
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
                    island = new Image("island1.png");
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
        numberOfPlayers = modelCache.getPlayersList().size();
        for (int i = 0; i < numberOfPlayers; i++) {
            ImageView cloudImageView = new ImageView(cloud);
            cloudImageView.setFitHeight(cloudHeight);
            cloudImageView.setFitWidth(cloudWidth);
            StackPane cloudStack = new StackPane(cloudImageView, cloudComponents(i));
            cloudStack.relocate(
                    getCoordinatesX(centerX, cloudRadius, i, numberOfPlayers),
                    getCoordinatesY(centerY, cloudRadius, i, numberOfPlayers));
            cloudButton(cloudStack, i);
            clouds.add(cloudStack);
        }
        return clouds;
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
        if (hasTowers(modelCache.getIslands().get(j))) {
            HBox tower = towerCounter(modelCache.getIslands().get(j).getColorOfTowers(), modelCache.getIslands().get(j).getNumberOfTowers());
            if (tower != null) {
                tower.relocate(
                        getCoordinatesX(islandContentCenter, islandContentRadius, i, islandContentComponents),
                        getCoordinatesY(islandContentCenter, islandContentRadius, i, islandContentComponents));
            }
            hBoxComponents.add(tower);
        }
        i++;
        if (hasNoEntry(modelCache.getIslands().get(j))) {
            ImageView noEntryImage = new ImageView(new Image("noEntry.png"));
            noEntryImage.setFitHeight(playerContentHeight);
            noEntryImage.setFitWidth(playerContentWidth);
            HBox noEntry = counterText(modelCache.getIslands().get(j).getNumberOfNoEntries(), noEntryImage);
            noEntry.relocate(
                    getCoordinatesX(islandContentCenter, islandContentRadius, i, islandContentComponents),
                    getCoordinatesY(islandContentCenter, islandContentRadius, i, islandContentComponents));
            hBoxComponents.add(noEntry);
        }
        ans.getChildren().addAll(hBoxComponents);
        return ans;
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
        List<VBox> characterImages = new ArrayList<>();
        for (CharacterInformation c : modelCache.getCharacters()) {
            character = new ImageView(new Image(c.getName().getImage()));
            character.setFitWidth(characterWidth);
            character.setFitHeight(characterHeight);
            character.setStyle(borderUnselected);
            VBox container = new VBox(character);
            Tooltip tooltip = new Tooltip(CharactersTooltips.getToolTip(c.getName()));
            Tooltip.install(container, tooltip);
            container.setAlignment(Pos.CENTER);
            if (c.getName().equals(Name.JOKER)) {
                container.getChildren().add(createCharacterCreature(modelCache.getJokerCreatures()));
            }
            if (c.getName().equals(Name.MONK)) {
                container.getChildren().add(createCharacterCreature(modelCache.getMonkCreatures()));
            }
            if (c.getName().equals(Name.PRINCESS)) {
                container.getChildren().add(createCharacterCreature(modelCache.getPrincessCreatures()));
            }
            if (c.getName().equals(Name.HERBALIST)) {
                container.getChildren().add(createNoEntryCharacter(modelCache.getDeactivators()));
            }
            container.setOnMouseClicked(e -> {
                if (clientState.isSelectCharacter()) {
                    guiEvents.add(playCharacterCode + commandSeparator + c.getIndex());
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
                if (guiPhases == GUIPhases.SELECT_CREATURE || guiPhases == GUIPhases.SELECT_DESTINATION) {
                    createdCommand = moveStudentsCode + commandSeparator;
                    createdCommand += creatureCode(c);
                    guiPhases = GUIPhases.SELECT_DESTINATION;
                } else if (guiPhases == GUIPhases.SELECT_CREATURE_FOR_CHARACTER) {
                    createdCommand = selectCreatureCode;
                    createdCommand += creatureCode(c);
                    guiPhases = GUIPhases.SELECT_DESTINATION_ISLAND;
                } else if (guiPhases == GUIPhases.SELECT_SOURCE_CREATURE) {
                    createdCommand += creatureCode(c);
                    guiEvents.add(createdCommand);
                    createdCommand = "";
                } else if (guiPhases == GUIPhases.SELECT_SOURCE_CREATURE_TO_SWAP &&
                        creaturesToSwap <= clientState.getCurrentPlayedCharacter().getMaxMoves()) {
                    createdCommand += creatureCode(c);
                    if (creaturesToSwap % clientState.getCurrentPlayedCharacter().getMaxMoves() != 0) {
                        createdCommand += creatureSeparator;
                    }
                    creaturesToSwap++;
                }

            });
            container.setStyle(borderUnselected);
            container.setOnMouseMoved(e -> {
                if (!clientState.getHeaders().equals(Headers.planning) && (clientState.getModelPayload().getCurrentPlayerUsername().equals(MY_USERNAME))) {
                    container.setStyle(borderSelected);
                }
            });
            container.setOnMouseExited(e -> {
                container.setStyle(borderUnselected);
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
        quit.setOnAction(e -> quitStage(stage));

        HBox currentPhaseBox = createCurrentPhaseBox(clientState);

        HBox actionPhaseBox = createActionPhaseBox(clientState);

        HBox currentPlayerBox = createCurrentPlayerBox(modelCache, MY_USERNAME);

        HBox topMenu;
        if (modelCache.isAdvancedRules()) {
            HBox bank = createBankComponent(modelCache.getCoinReserve());
            topMenu = new HBox(currentPlayerBox, actionPhaseBox, currentPhaseBox, bank, quit);
        } else {
            topMenu = new HBox(currentPlayerBox, actionPhaseBox, currentPhaseBox, quit);
        }
        topMenu.setAlignment(Pos.CENTER);
        topMenu.setSpacing(50);
        root.setTop(topMenu);
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
            HBox player = new HBox();
            player.getChildren().addAll(playerInfo,
                    createComponentWithAssistant(p.getLastPlayedCards()),
                    createComponentWithCreatures(entranceHeaderText, p),
                    createComponentWithCreatures(diningRoomHeaderText, p),
                    createComponentWithCreatures(professorsHeaderText, p),
                    createTowerComponent(p));
            if (modelCache.isAdvancedRules()) {
                player.getChildren().add(createCoinComponent(p.getMyCoins()));
            }

            player.setSpacing(smallSpacing);
            player.setAlignment(Pos.CENTER);
            playerList.getChildren().add(player);
        }
        return playerList;
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
        if (name.equals(diningRoomHeaderText) && p.getUsername().equals(MY_USERNAME)) {
            ans.setOnMouseMoved(e -> {
                if (guiPhases == GUIPhases.SELECT_DESTINATION) {
                    ans.setStyle(borderSelected);
                }

            });
            ans.setOnMouseClicked(e -> {
                if (guiPhases == GUIPhases.SELECT_DESTINATION) {
                    createdCommand += commandSeparator;
                    createdCommand += String.valueOf(0);
                    guiEvents.add(createdCommand);
                    createdCommand = "";
                    guiPhases = GUIPhases.END;
                }
            });
            ans.setOnMouseExited(e -> {
                ans.setStyle(defaultComponentLayout);
            });
        }
        return ans;
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
            if (clientState.isMoveMotherNature() || guiPhases == GUIPhases.SELECT_DESTINATION ||
                    guiPhases == GUIPhases.SELECT_DESTINATION_ISLAND || guiPhases == GUIPhases.SELECT_ISLAND) {
                object.setStyle(borderSelected);
            }
        });
        object.setOnMouseExited(e -> {
            object.setStyle(noBorder);
        });
        object.setOnMouseClicked(e -> {
            createIslandCommand(i);
        });
    }

    /**
     * This method creates the right command to send to the server according to the gui's phase.
     *
     * @param i the index of the island chosen
     */
    private void createIslandCommand(int i) {
        if (guiPhases == GUIPhases.SELECT_DESTINATION) {
            createdCommand += commandSeparator;
            createdCommand += String.valueOf(i + 1);
            guiEvents.add(createdCommand);
            createdCommand = "";
        } else if (guiPhases == GUIPhases.SELECT_DESTINATION_ISLAND) {
            createdCommand += selectDestinationIslandCode;
            createdCommand += String.valueOf(i + 1);
            guiEvents.add(createdCommand);
            createdCommand = "";
        } else if (GUIPhases.SELECT_ISLAND == guiPhases) {
            createdCommand += String.valueOf(i + 1);
            guiEvents.add(createdCommand);
            createdCommand = "";
        } else if (clientState.isMoveMotherNature()) {
            int mnPosition = clientState.getModelPayload().getMotherNature();
            createdCommand += moveMotherNatureCode + commandSeparator;
            createdCommand += String.valueOf(evaluateMnJumps(mnPosition, i, clientState));
            guiEvents.add(createdCommand);
            createdCommand = "";
        }
        guiPhases = GUIPhases.END;

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
                createdCommand += selectCloudCode + commandSeparator;
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
        if(clientState!=null){
            if (clientState.isMoveStudents()) {
                guiPhases = GUIPhases.SELECT_CREATURE;
            }
        }
    }

    /**
     * Sets the given gui phase
     * @param guiPhases is the given phase
     */
    public void setGuiPhases(GUIPhases guiPhases) {
        this.guiPhases = guiPhases;
    }


    /**
     * Shows the alert to the player to inform the character behaviour.
     * Creates the confirm button for the provided selection.
     */
    public void characterNeedsMMNMovements() {
        Text text = new Text("Select the number of steps you want mother nature to do.");
        text.setStyle(bodyFont);
        guiPhases = GUIPhases.SELECT_MMN_MOVEMENTS;
        ChoiceBox<String> prompt = new ChoiceBox<>();
        prompt.getItems().addAll(firstPostmanCode, secondPostmanCode, thirdPostmanCode);
        Button button = new Button();
        button.setText(confirmText);
        button.setOnAction(e -> {
            switch (prompt.getValue()) {
                case firstPostmanCode:
                    createdCommand += firstPostmanCode;
                    break;
                case secondPostmanCode:
                    createdCommand += secondPostmanCode;
                    break;
                case thirdPostmanCode:
                    createdCommand += thirdPostmanCode;
                    break;
                default:
                    break;
            }
            guiEvents.add(createdCommand);
            guiPhases = GUIPhases.END;
            createdCommand = "";
            root.setBottom(new HBox());
        });
        HBox postmanSelection = new HBox(text, prompt, button);
        root.setBottom(postmanSelection);
    }

    /**
     *
     * @param string to be added to the current command string
     */
    public void addCreatedCommand(String string) {
        createdCommand += string;
    }

    /**
     * Creates the swap button.
     *
     * @param isFirstSelection distinguishes between first and second selection phase
     */
    public void createSwapButton(boolean isFirstSelection) {
        Button button = new Button();
        button.setText("Confirm the selected creatures");
        button.setOnAction(e -> {
            if (isFirstSelection) {
                createdCommand += commandSeparator + selectDestinationText + commandSeparator;
                creaturesToSwap = 1;
                createAlertForSwapCommand(clientState);
                createSwapButton(false);
            } else {
                guiEvents.add(createdCommand);
                createdCommand = "";
                guiPhases = GUIPhases.END;
                root.setBottom(new HBox());
                creaturesToSwap = 1;
            }

        });
        HBox postmanSelection = new HBox(button);
        root.setBottom(postmanSelection);
    }

}



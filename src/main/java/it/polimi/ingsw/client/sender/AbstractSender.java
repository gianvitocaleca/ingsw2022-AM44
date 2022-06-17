package it.polimi.ingsw.client.sender;

import com.google.gson.Gson;
import it.polimi.ingsw.pingHandler.ClientPingHandler;
import it.polimi.ingsw.client.ClientState;
import it.polimi.ingsw.pingHandler.PingState;
import it.polimi.ingsw.server.model.enums.Creature;
import it.polimi.ingsw.server.model.enums.Name;
import it.polimi.ingsw.server.networkMessages.*;
import it.polimi.ingsw.server.networkMessages.payloads.ActionAnswerPayload;
import it.polimi.ingsw.server.networkMessages.payloads.CharactersParametersPayload;
import it.polimi.ingsw.server.networkMessages.payloads.PlanningAnswerPayload;
import it.polimi.ingsw.server.networkMessages.payloads.StringPayload;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;

public abstract class AbstractSender {
    protected String ip;
    protected int port;
    protected ClientState cs;
    protected Gson gson;
    protected PingState ps;
    protected final int pingTime = 5000;
    protected final int maxNoAnswers = 4;
    protected Socket socket;
    protected PrintWriter socketOut;
    protected final String error = "error";
    protected final String moveStudentsText = "ms";
    protected final String moveMotherNatureText = "mmn";
    protected final String selectCloudText = "sc";
    protected final String playCharacterText = "pc";
    protected final String commandSeparator = "-";
    protected final String selectCreatureText = "c";
    protected final String selectIslandText = "i";
    protected final String selectDestinationText = "d";
    protected final String creatureSeparator = ",";
    protected final String quitCommandText = "quit";


    public AbstractSender(String ip, int port) {
        this.ip = ip;
        this.port = port;
        gson = new Gson();
        ps = new PingState();
        socket = new Socket();
    }

    public void startClient() throws IOException {
        cs = new ClientState();
        socket = new Socket(ip, port);
        System.out.println("Connection established");
        System.out.println("Client dynamic port number: " + socket.getLocalPort());
        Thread t1 = new Thread(new ClientPingHandler(ps, socket, pingTime, maxNoAnswers));
        t1.start();
    }

    abstract void play(Socket socket);

    protected String encodeMessage(String string) {
        if (string == null) return error;
        if (string.equalsIgnoreCase(quitCommandText)) {
            System.exit(0);
        }
        if (!cs.getCurrentPlayer()) {
            System.out.println("You are not the current player! Please wait");
            return error;
        }
        if (cs.getHeaders().equals(Headers.planning)) {
            try {
                int temp = Integer.parseInt(string);
                return gson.toJson(new Message(cs.getHeaders(), new PlanningAnswerPayload(cs.getUsername(), temp)));
            } catch (NumberFormatException ignore) {
                System.out.println("Please provide a valid selection number!");
                return error;
            }
        }
        if (cs.getHeaders().equals(Headers.action)) {
            List<String> result = Arrays.stream(string.split(commandSeparator)).toList();
            if (result.size() > 0) {
                switch (result.get(0).toLowerCase()) {
                    case moveStudentsText:
                        if (cs.isMoveStudents() && result.size() == 3) {
                            return createMoveStudentMessage(result);
                        }
                        break;
                    case moveMotherNatureText:
                        if (cs.isMoveMotherNature() && result.size() == 2) {
                            return createMessage(result, true, false, false);
                        }
                        break;
                    case selectCloudText:
                        if (cs.isSelectCloud() && result.size() == 2) {
                            return createMessage(result, false, true, false);
                        }
                        break;
                    case playCharacterText:
                        if (cs.isSelectCharacter() && result.size() == 2) {
                            return createMessage(result, false, false, true);
                        }
                        break;
                }
            }
            return badGuysHandler();
        }
        if (cs.getHeaders().equals(Headers.characterPlayed)) {
            List<String> result = Arrays.stream(string.split(commandSeparator)).toList();
            if (result.size() == 1) {
                int selectedNumber;
                try {
                    selectedNumber = Integer.parseInt(result.get(0));
                    if (cs.getCurrentPlayedCharacter().equals(Name.HERBALIST) ||
                            cs.getCurrentPlayedCharacter().equals(Name.HERALD) ||
                            cs.getCurrentPlayedCharacter().equals(Name.MAGICPOSTMAN)) {
                        return createCharIntMessage(selectedNumber);
                    }
                } catch (NumberFormatException ignore) {
                    Optional<Creature> ans = creatureFromCli(result.get(0));
                    if (!ans.isEmpty()) {
                        return createCreatureCharMessage(ans.get());
                    }
                    return badGuysHandler();
                }
            } else if (result.size() == 4) {
                switch (cs.getCurrentPlayedCharacter()) {
                    case MONK:
                        if (result.get(0).equalsIgnoreCase(selectCreatureText)) {
                            Optional<Creature> creature = creatureFromCli(result.get(1));
                            if (creature.isPresent()) {
                                if (result.get(2).equalsIgnoreCase(selectIslandText)) {
                                    int providedInteger;
                                    try {
                                        providedInteger = Integer.parseInt(result.get(3));
                                        return createMonkMessage(creature.get(), providedInteger);
                                    } catch (NumberFormatException ignore) {
                                        return badGuysHandler();
                                    }

                                }
                                return badGuysHandler();
                            }
                            return badGuysHandler();
                        }
                        return badGuysHandler();
                    case MINSTREL:
                    case JOKER:
                        return createSwapMessage(result, cs.getCurrentPlayedCharacter().getMaxMoves());
                    default:
                        return badGuysHandler();
                }
            } else {
                return badGuysHandler();
            }

        }
        return gson.toJson(new Message(cs.getHeaders(), new StringPayload(string)));
    }

    private String createSwapMessage(List<String> result, int maxMoves) {
        if (result.get(0).equalsIgnoreCase(selectCreatureText) && result.get(2).equalsIgnoreCase(selectDestinationText)) {
            List<String> sourceCreatures = Arrays.stream(result.get(1).split(creatureSeparator)).toList();
            List<String> destinationCreatures = Arrays.stream(result.get(3).split(creatureSeparator)).toList();
            if (sourceCreatures.size() == destinationCreatures.size() && sourceCreatures.size() <= maxMoves) {
                List<Creature> sC = new ArrayList<>();
                List<Creature> dC = new ArrayList<>();
                for (int i = 0; i < sourceCreatures.size(); i++) {
                    if (creatureFromCli(sourceCreatures.get(i)).isPresent() && creatureFromCli(destinationCreatures.get(i)).isPresent()) {
                        sC.add(creatureFromCli(sourceCreatures.get(i)).get());
                        dC.add(creatureFromCli(destinationCreatures.get(i)).get());
                    } else {
                        return badGuysHandler();
                    }
                }
                return gson.toJson(new Message(cs.getHeaders(),
                        new CharactersParametersPayload(sC, 0, 0, dC)));
            }
            return badGuysHandler();
        }
        return badGuysHandler();
    }

    protected void setUsername(String username) {
        if (cs.getHeaders().equals(Headers.loginMessage_Username)) {
            cs.setUsername(username.toLowerCase());
        }
    }


    private Optional<Creature> creatureFromCli(String provided) {
        Optional<Creature> ans = Optional.empty();
        switch (provided) {
            case "R":
                ans = Optional.of(Creature.RED_DRAGONS);
                break;
            case "G":
                ans = Optional.of(Creature.GREEN_FROGS);
                break;
            case "B":
                ans = Optional.of(Creature.BLUE_UNICORNS);
                break;
            case "Y":
                ans = Optional.of(Creature.YELLOW_GNOMES);
                break;
            case "P":
                ans = Optional.of(Creature.PINK_FAIRIES);
                break;
            default:
                break;
        }
        return ans;
    }

    private String createMoveStudentMessage(List<String> result) {
        Optional<Creature> selection = creatureFromCli(result.get(1).toUpperCase());
        if (selection.isEmpty()) {
            return badGuysHandler();
        }
        int providedDestination;
        boolean isDiningRoomDestination = false;
        try {
            providedDestination = Integer.parseInt(result.get(2));
        } catch (NumberFormatException ignore) {
            System.out.println("Why did you do it!");
            System.out.println("(ง •̀_•́)ง");
            return error;
        }
        if (providedDestination == 0) {
            isDiningRoomDestination = true;
        }
        return gson.toJson(new Message(cs.getHeaders(),
                new ActionAnswerPayload(true, false, false,
                        false, isDiningRoomDestination, selection.get(), providedDestination - 1)));
    }

    private String createMessage(List<String> result, boolean isMMN, boolean isSC, boolean isPC) {
        int providedIndex;
        try {
            providedIndex = Integer.parseInt(result.get(1));
        } catch (NumberFormatException ignore) {
            System.out.println("Why did you do it!");
            System.out.println("(ง •̀_•́)ง");
            return error;
        }
        return gson.toJson(new Message(cs.getHeaders(), new ActionAnswerPayload(false, isMMN, isSC,
                isPC, false, Creature.RED_DRAGONS, providedIndex)));
    }

    private String createCharIntMessage(int selectedNumber) {
        if (cs.getCurrentPlayedCharacter().equals(Name.MAGICPOSTMAN)) {
            return gson.toJson(new Message(cs.getHeaders(),
                    new CharactersParametersPayload(new ArrayList<>(), 0, selectedNumber, new ArrayList<>())));
        }
        return gson.toJson(new Message(cs.getHeaders(),
                new CharactersParametersPayload(new ArrayList<>(), selectedNumber, 0, new ArrayList<>())));
    }

    private String createCreatureCharMessage(Creature creature) {
        List<Creature> creatureList = new ArrayList<>();
        creatureList.add(creature);
        return gson.toJson(new Message(cs.getHeaders(),
                new CharactersParametersPayload(creatureList, 0, 0, new ArrayList<>())));
    }

    private String createMonkMessage(Creature creature, int island) {
        List<Creature> creatureList = new ArrayList<>();
        creatureList.add(creature);
        return gson.toJson(new Message(cs.getHeaders(),
                new CharactersParametersPayload(creatureList, island, 0, new ArrayList<>())));
    }

    private String badGuysHandler() {
        System.out.println("Bad syntax!");
        System.out.println("(╯°□°）╯$ $ $");
        return error;
    }

    public void closeConnection() throws IOException {
        socket.close();
    }
}

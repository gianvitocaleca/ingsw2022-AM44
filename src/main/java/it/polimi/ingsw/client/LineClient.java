package it.polimi.ingsw.client;

import com.google.gson.Gson;
import it.polimi.ingsw.pingHandler.PingState;
import it.polimi.ingsw.server.model.enums.Creature;
import it.polimi.ingsw.server.networkMessages.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class LineClient {
    private String ip;
    private int port;
    private String inputLine;
    private ClientState cs;
    private Gson gson;
    private PingState ps;
    private final int pingTime = 5000;
    private final int maxNoAnswers = 4;
    private Scanner stdin;
    private PrintWriter socketOut;

    private String error = "error";

    public LineClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
        gson = new Gson();
        ps = new PingState();
    }

    public void startClient() throws IOException {
        cs = new ClientState();
        Socket socket = new Socket(ip, port);
        System.out.println("Connection established");
        System.out.println("Client dynamic port number: " + socket.getLocalPort());
        Scanner s = new Scanner(socket.getInputStream());
        Thread t = new Thread(new MessageReceiverClient(s, cs, ps));
        t.start();
        Thread t1 = new Thread(new ClientPingHandler(ps, socket, pingTime, maxNoAnswers));
        t1.start();
        socketOut = new PrintWriter(socket.getOutputStream());
        stdin = new Scanner(System.in);
        play(socket);
    }

    private void play(Socket socket) {
        while (!ps.isCloseConnection()) {
            String result;
            do {
                inputLine = stdin.nextLine();
                result = encodeMessage(inputLine);
            } while (result.equals(error));
            socketOut.println(result);
            if (cs.getHeaders().equals(Headers.loginMessage_Username)) {
                setUsername(inputLine);
            }
            if (socketOut.checkError()) {
                System.out.println("There is an error with the server.. closing connection");
                break;
            }
        }
        while (!ps.isCloseConnection()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        stdin.close();
        socketOut.close();
        try {
            socket.close();
            System.out.println("Connection closed, server unreachable");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String encodeMessage(String string) {
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
            List<String> result = Arrays.stream(string.split(":")).toList();
            switch (result.get(0).toUpperCase()) {
                case "MS":
                    if (cs.isMoveStudents()) {
                        return createMoveStudentMessage(result);
                    }
                    break;
                case "MMN":
                    if (cs.isMoveMotherNature()) {
                        return createMessage(result, true, false, false);
                    }
                    break;
                case "SC":
                    if (cs.isSelectCloud()) {
                        return createMessage(result, false, true, false);
                    }
                    break;
                case "PC":
                    if (cs.isSelectCharacter()) {
                        return createMessage(result, false, false, true);
                    }
                    break;
                default:
                    System.out.println("Wrong command syntax, please try again");
            }
            System.out.println("Why did you ignore the instructions ?");
            System.out.println("(╯°□°）╯$ $ $");
            return error;
        }
        return gson.toJson(new Message(cs.getHeaders(), new StringPayload(string)));
    }

    private void setUsername(String username) {
        if (cs.getHeaders().equals(Headers.loginMessage_Username)) {
            cs.setUsername(username);
        }
    }

    private void playAssistant() {
        System.out.println("Which assistant do you want to play?");
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
            return error;
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
        int providedMovement;
        try {
            providedMovement = Integer.parseInt(result.get(1));
        } catch (NumberFormatException ignore) {
            System.out.println("Why did you do it!");
            System.out.println("(ง •̀_•́)ง");
            return error;
        }
        return gson.toJson(new Message(cs.getHeaders(), new ActionAnswerPayload(false, isMMN, isSC,
                isPC, false, Creature.RED_DRAGONS, providedMovement)));
    }

}

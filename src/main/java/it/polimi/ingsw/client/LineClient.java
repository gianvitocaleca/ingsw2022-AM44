package it.polimi.ingsw.client;

import com.google.gson.Gson;
import it.polimi.ingsw.pingHandler.PingState;
import it.polimi.ingsw.server.networkMessages.Headers;
import it.polimi.ingsw.server.networkMessages.Message;
import it.polimi.ingsw.server.networkMessages.PlanningAnswerPayload;
import it.polimi.ingsw.server.networkMessages.StringPayload;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
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


}

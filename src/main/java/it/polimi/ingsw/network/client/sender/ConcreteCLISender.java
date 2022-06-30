package it.polimi.ingsw.network.client.sender;

import it.polimi.ingsw.network.client.receiver.ConcreteCLIReceiver;
import it.polimi.ingsw.network.server.networkMessages.Headers;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;

/**
 * This class transform the client's string into a json string, then sends it to the server.
 */
public class ConcreteCLISender extends AbstractSender {
    private String inputLine;
    private Scanner stdin;
    private final String error = "error";

    /**
     * Waits for commands from the player.
     * Encodes and sends the messages on the network.
     * @param ip the server address
     * @param port the server port
     */
    public ConcreteCLISender(String ip, int port) {
        super(ip, port);
    }

    /**
     * Starts to handle the player's commands
     * @throws IOException
     */
    @Override
    public void startClient() throws IOException {
        super.startClient();
        Scanner s = new Scanner(socket.getInputStream());
        Thread t = new Thread(new ConcreteCLIReceiver(s, cs, ps));
        t.start();
        socketOut = new PrintWriter(socket.getOutputStream());
        stdin = new Scanner(System.in);
        play(socket);
    }

    /**
     * Starts to listen for player inputs, until the connection is closed
     * The command is formatted if it's allowed and correct.
     *
     * @param socket
     */
    public void play(Socket socket) {
        if (!cs.getDisconnection()) {
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
            System.out.println("The connection is closed: " + ps.isCloseConnection());
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
}

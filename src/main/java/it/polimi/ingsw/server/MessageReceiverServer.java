package it.polimi.ingsw.server;

import it.polimi.ingsw.server.controller.events.MessageReceivedEvent;
import it.polimi.ingsw.server.viewProxy.MessageHandler;

import javax.swing.event.EventListenerList;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MessageReceiverServer extends Thread {

    private Socket socket;

    private EventListenerList listeners = new EventListenerList();

    public MessageReceiverServer(Socket socket, MessageHandler listener) {
        this.socket = socket;
        listeners.add(MessageHandler.class, listener);
    }

    @Override
    public void run() {
        while (true) {
            try {
                Scanner in = new Scanner(socket.getInputStream());
                String line = in.nextLine();
                MessageReceivedEvent evt = new MessageReceivedEvent(this, line);
                for (MessageHandler event : listeners.getListeners(MessageHandler.class)) {
                    event.eventPerformed(evt, socket);
                }
            } catch (IOException ignore) {
                System.out.println("errore lettura server");
            }
        }

    }

}

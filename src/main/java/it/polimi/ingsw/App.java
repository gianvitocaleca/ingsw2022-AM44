package it.polimi.ingsw;

import it.polimi.ingsw.client.ClientMain;
import it.polimi.ingsw.server.ServerMain;
import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.server.model.GameModel;
import it.polimi.ingsw.server.model.enums.Color;
import it.polimi.ingsw.server.model.enums.Wizard;
import it.polimi.ingsw.server.networkMessages.StringPayload;
import it.polimi.ingsw.server.viewProxy.MessageHandler;

import java.util.*;

public class App {

    private static final String client = "client";

    private static final String server = "server";

    private static final String address = "loopback";

    private static String loopback = "127.0.0.1";

    private static final String port = "default";

    private static int defaultPort = 1337;

    public static void main(String[] args) {

        if (args.length > 0) {
            switch (args[0]) {
                case client:
                    ClientMain clientMain = new ClientMain();
                    if (args.length > 1) {
                        if (address.equals(args[1])) {
                            clientStart(args, clientMain, loopback);
                        } else {
                            List<String> providedAddress = Arrays.stream(args[1].split(".")).toList();
                            if (providedAddress.size() != 4) {
                                System.out.println("Please provide a valid address!");
                                return;
                            }
                            int addressNumber;
                            for (String s : providedAddress) {
                                try {
                                    addressNumber = Integer.parseInt(s);
                                } catch (NumberFormatException ignore) {
                                    System.out.println("Please provide a valid address!");
                                    return;
                                }
                                if (addressNumber < 0 || addressNumber > 255) {
                                    System.out.println("Please provide a valid address!");
                                    return;
                                }
                            }
                            clientStart(args, clientMain, args[1]);
                        }
                    }
                    clientStart(args, clientMain, loopback);
                    break;
                case server:
                    ServerMain serverMain = new ServerMain();
                    if (args.length > 1) {
                        if (args[1].equals(port)) {
                            serverMain.start(defaultPort);
                        }
                        int providedServerPort;
                        try {
                            providedServerPort = Integer.parseInt(args[1]);
                        } catch (NumberFormatException ignore) {
                            System.out.println("Please provide a valid port!");
                            return;
                        }
                        serverMain.start(providedServerPort);
                    }
                    System.out.println("Starting on default port :" + defaultPort);
                    serverMain.start(defaultPort);
                    break;
                default:
                    System.out.println("Invalid arguments");
                    System.out.println("Please provide");
                    System.out.println("For client :=: client <address> <port>");
                    System.out.println("For server :=: server <port>");
                    break;
            }
        }

    }

    private static void clientStart(String[] args, ClientMain clientMain, String address) {
        if (args.length > 2) {
            switch (args[2]) {
                case port:
                    clientMain.start(address, defaultPort);
                    break;
                default:
                    int temp;
                    try {
                        temp = Integer.parseInt(args[2]);
                    } catch (NumberFormatException ignore) {
                        System.out.println("Provide a valid port number!");
                        return;
                    }
                    clientMain.start(address, temp);
            }
        } else {
            System.out.println("Starting on default port :" + defaultPort);
            clientMain.start(address, defaultPort);
        }
    }


}

package it.polimi.ingsw;

import it.polimi.ingsw.client.ClientCliMain;
import it.polimi.ingsw.client.ClientGuiMain;
import it.polimi.ingsw.server.ServerMain;

import java.util.*;

public class App {

    private static final String client = "client";

    private static final String server = "server";

    private static final String address = "loopback";

    private static String loopback = "127.0.0.1";

    private static final String port = "default";
    private static final String Gui = "gui";
    private static boolean isGui = false;

    private static int defaultPort = 1337;

    public static void main(String[] args) {

        if (args.length > 0) {
            switch (args[0]) {
                case client:
                    if (args.length > 1) {
                        if (Gui.equals(args[1])) {
                            isGui = true;
                        } else {
                            isGui = false;
                        }
                        if (args.length > 2) {
                            if (address.equals(args[2])) {
                                clientStart(args, loopback);
                            } else {
                                List<String> providedAddress = Arrays.stream(args[2].split("\\.")).toList();
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
                                clientStart(args, args[2]);
                            }
                        }

                    }
                    clientStart(args, loopback);
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

    private static void clientStart(String[] args, String address) {
        int providedPort = defaultPort;
        if (args.length > 3) {
            switch (args[3]) {
                case port:
                    break;
                default:
                    int temp;
                    try {
                        temp = Integer.parseInt(args[3]);
                    } catch (NumberFormatException ignore) {
                        System.out.println("Provide a valid port number!");
                        return;
                    }
                    providedPort = temp;
            }
        } else {
            System.out.println("Starting on default port :" + defaultPort);
        }
        if (isGui) {
            ClientGuiMain.start(address, providedPort);
        } else {
            ClientCliMain.start(address, providedPort);
        }
    }


}

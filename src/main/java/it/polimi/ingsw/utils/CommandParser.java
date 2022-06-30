package it.polimi.ingsw.utils;

import java.util.*;

import static it.polimi.ingsw.utils.TextAssets.*;

public class CommandParser {

    private final List<String> arguments;
    private final Map<String, String> map = new TreeMap<>();
    private final Set<String> flags = new TreeSet<>();
    private final String flagCommand = "-";
    private final String emptyString = "";

    /**
     * Registers the given arguments
     *
     * @param arguments are given by the player
     */
    public CommandParser(String[] arguments) {
        this.arguments = Arrays.asList(arguments);
        map();
    }

    /**
     * Puts the flags in a set. Puts the arguments in a map.
     */
    private void map() {
        for (String a : arguments) {
            if (a.startsWith(flagCommand)) {
                if (arguments.indexOf(a) == (arguments.size() - 1)) {
                    flags.add(a.replace(flagCommand, emptyString));
                } else if (arguments.get(arguments.indexOf(a) + 1).startsWith(flagCommand)) {
                    flags.add(a.replace(flagCommand, emptyString));
                } else {
                    map.put(a.replace(flagCommand, emptyString), arguments.get(arguments.indexOf(a) + 1));
                }
            }
        }
    }

    /**
     * Queries the map.
     *
     * @param flag is the given key
     * @return is the content or an empty string
     */
    private String getArgumentOfFlag(String flag) {
        if (map.containsKey(flag)) {
            return map.get(flag);
        }
        return emptyString;
    }

    /**
     * Queries the set.
     *
     * @param flag is the given flag
     * @return whether is present
     */
    public boolean flagIsPresent(String flag) {
        return flags.contains(flag);
    }

    /**
     * @return whether there's the server flag
     */
    public boolean isServer() {
        return flagIsPresent(server) || flagIsPresent(shortServer);
    }

    /**
     * @return is the given port or the default
     */
    public String getPort() {
        String ans = getArgumentOfFlag(port);
        if (!ans.equals(emptyString)) {
            return ans;
        }
        ans = getArgumentOfFlag(shortPort);
        if (!ans.equals(emptyString)) {
            return ans;
        }
        return defaultPort;
    }

    /**
     * @return is the given address or the loopback
     */
    public String getAddress() {
        String ans = getArgumentOfFlag(client);
        if (!ans.equals(emptyString)) {
            return ans;
        }
        ans = getArgumentOfFlag(shortClient);
        if (!ans.equals(emptyString)) {
            return ans;
        }
        return loopback;
    }

    /**
     * @return whether there's the gui flag
     */
    public boolean isGui() {
        return flagIsPresent(gui) || flagIsPresent(shortGui);
    }

    /**
     * @return is the given scale or the default
     */
    public String getScale() {
        String ans = getArgumentOfFlag(scale);
        if (!ans.equals(emptyString)) {
            return ans;
        }
        ans = getArgumentOfFlag(shortScale);
        if (!ans.equals(emptyString)) {
            return ans;
        }
        return defaultScale;
    }
}

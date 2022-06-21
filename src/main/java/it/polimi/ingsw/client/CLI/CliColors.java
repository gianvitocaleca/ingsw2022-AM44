package it.polimi.ingsw.client.CLI;

public enum CliColors {
    ERASE_SCREEN("\u001b[H\u001b[2J"),

    BG_BLACK("\u001b[40m"),
    BG_RED("\u001b[41m"),
    BG_GREEN("\u001b[42m"),
    BG_YELLOW("\u001b[43m"),
    BG_BLUE("\u001b[44m"),
    BG_PINK("\u001b[45m"),
    BG_WHITE("\u001b[47m"),

    FG_BLACK("\u001b[30m"),
    FG_RED("\u001b[31m"),
    FG_GREEN("\u001b[32m"),
    FG_YELLOW("\u001b[33m"),
    FG_BLUE("\u001b[34m"),
    FG_PINK("\u001b[35m"),
    FG_CYAN("\u001b[96m"),
    FG_GRAY("\u001b[38;5;250m"),
    FG_WHITE("\u001b[97m"),
    FG_BORDER("\u001b[38;5;238m"),
    FG_TITLE("\u001b[38;5;56m"),
    FG_MN("\u001b[38;5;130m"),

    BOLD("\u001b[1m"),
    DIM("\u001b[2m"),
    ITALIC("\u001b[3m"),
    UNDERLINE("\u001b[4m"),
    BLINKING("\u001b[5m"),
    REVERSE("\u001b[7m"),
    INVISIBLE("\u001b[8m"),

    RST("\u001b[0m");


    private final String code;

    /**
     * @param code is the escape sequence
     */
    CliColors(String code) {
        this.code = code;
    }

    /**
     * @return is the escape sequence
     */
    public String getCode() {
        return code;
    }
}

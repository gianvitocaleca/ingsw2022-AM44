package it.polimi.ingsw.view.CLI;

public enum CliColors {
    ERASE_SCREEN("\u001b[H\u001b[2J"),
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

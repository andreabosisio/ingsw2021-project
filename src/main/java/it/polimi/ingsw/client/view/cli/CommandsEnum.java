package it.polimi.ingsw.client.view.cli;

/**
 * Contains the possible commands of the CLI.
 */
public enum CommandsEnum {
    MARKET("MARKET"),
    BUY("BUY"),
    PRODUCTION("PRODUCTION"),
    LEADER("LEADER"),
    SEE("SEE"),
    DONE("DONE"),
    BACK("BACK"),
    REFRESH("REFRESH"),
    ACTIVATE("ACTIVATE"),
    DISCARD("DISCARD"),
    GRIDS("GRIDS"),
    PLAYER("PLAYER");

    private final String cmd;

    CommandsEnum(final String cmd) {
        this.cmd = cmd;
    }

    /**
     * @return the String of the Command
     */
    @Override
    public String toString() {
        return cmd;
    }
}

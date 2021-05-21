package it.polimi.ingsw.client.view.cli;

import java.util.Locale;

public enum AnsiEnum {

    LOGIN("\n" +
            "██╗      ██████╗  ██████╗ ██╗███╗   ██╗\n" +
            "██║     ██╔═══██╗██╔════╝ ██║████╗  ██║\n" +
            "██║     ██║   ██║██║  ███╗██║██╔██╗ ██║\n" +
            "██║     ██║   ██║██║   ██║██║██║╚██╗██║\n" +
            "███████╗╚██████╔╝╚██████╔╝██║██║ ╚████║\n" +
            "╚══════╝ ╚═════╝  ╚═════╝ ╚═╝╚═╝  ╚═══╝\n" +
            "                                       \n"),
    LOGIN_SMALL("\n" +
            "█░░ █▀█ █▀▀ █ █▄░█\n" +
            "█▄▄ █▄█ █▄█ █ █░▀█"),

    LOGO("\n" +
            "                ███╗   ███╗ █████╗ ███████╗███████╗████████╗██████╗ ██╗                                        \n" +
            "                ████╗ ████║██╔══██╗██╔════╝██╔════╝╚══██╔══╝██╔══██╗██║                                        \n" +
            "                ██╔████╔██║███████║█████╗  ███████╗   ██║   ██████╔╝██║                                        \n" +
            "                ██║╚██╔╝██║██╔══██║██╔══╝  ╚════██║   ██║   ██╔══██╗██║                                        \n" +
            "                ██║ ╚═╝ ██║██║  ██║███████╗███████║   ██║   ██║  ██║██║                                        \n" +
            "                ╚═╝     ╚═╝╚═╝  ╚═╝╚══════╝╚══════╝   ╚═╝   ╚═╝  ╚═╝╚═╝                                        \n" +
            "                                  ██████╗ ███████╗██╗                                                            \n" +
            "                                  ██╔══██╗██╔════╝██║                                                            \n" +
            "                                  ██║  ██║█████╗  ██║                                                            \n" +
            "                                  ██║  ██║██╔══╝  ██║                                                            \n" +
            "                                  ██████╔╝███████╗███████╗                                                       \n" +
            "                                  ╚═════╝ ╚══════╝╚══════╝                                                       \n" +
            "██████╗ ██╗███╗   ██╗ █████╗ ███████╗ ██████╗██╗███╗   ███╗███████╗███╗   ██╗████████╗ ██████╗ \n" +
            "██╔══██╗██║████╗  ██║██╔══██╗██╔════╝██╔════╝██║████╗ ████║██╔════╝████╗  ██║╚══██╔══╝██╔═══██╗\n" +
            "██████╔╝██║██╔██╗ ██║███████║███████╗██║     ██║██╔████╔██║█████╗  ██╔██╗ ██║   ██║   ██║   ██║\n" +
            "██╔══██╗██║██║╚██╗██║██╔══██║╚════██║██║     ██║██║╚██╔╝██║██╔══╝  ██║╚██╗██║   ██║   ██║   ██║\n" +
            "██║  ██║██║██║ ╚████║██║  ██║███████║╚██████╗██║██║ ╚═╝ ██║███████╗██║ ╚████║   ██║   ╚██████╔╝\n" +
            "╚═╝  ╚═╝╚═╝╚═╝  ╚═══╝╚═╝  ╚═╝╚══════╝ ╚═════╝╚═╝╚═╝     ╚═╝╚══════╝╚═╝  ╚═══╝   ╚═╝    ╚═════╝ \n" +
            "                                                                                               \n"),

    MARBLE(" ● "),

    LEFT_ARROW("←"),
    UP_ARROW("↑"),

    ;


    private final String asciiArt;

    public static final String CLEAR = "\u001b[{n}J";

    // Reset
    public static final String RESET = "\033[0m";  // Text Reset

    // Regular Colors
    public static final String BLACK = "\033[0;30m";   // BLACK
    public static final String RED = "\033[0;31m";     // RED
    public static final String GREEN = "\033[0;32m";   // GREEN
    public static final String YELLOW = "\033[0;33m";  // YELLOW
    public static final String BLUE = "\033[0;34m";    // BLUE
    public static final String PURPLE = "\033[0;35m";  // PURPLE
    public static final String CYAN = "\033[0;36m";    // CYAN
    public static final String WHITE = "\033[0;37m";   // WHITE

    // Bold
    public static final String BLACK_BOLD = "\033[1;30m";  // BLACK
    public static final String RED_BOLD = "\033[1;31m";    // RED
    public static final String GREEN_BOLD = "\033[1;32m";  // GREEN
    public static final String YELLOW_BOLD = "\033[1;33m"; // YELLOW
    public static final String BLUE_BOLD = "\033[1;34m";   // BLUE
    public static final String PURPLE_BOLD = "\033[1;35m"; // PURPLE
    public static final String CYAN_BOLD = "\033[1;36m";   // CYAN
    public static final String WHITE_BOLD = "\033[1;37m";  // WHITE

    // Underline
    public static final String BLACK_UNDERLINED = "\033[4;30m";  // BLACK
    public static final String RED_UNDERLINED = "\033[4;31m";    // RED
    public static final String GREEN_UNDERLINED = "\033[4;32m";  // GREEN
    public static final String YELLOW_UNDERLINED = "\033[4;33m"; // YELLOW
    public static final String BLUE_UNDERLINED = "\033[4;34m";   // BLUE
    public static final String PURPLE_UNDERLINED = "\033[4;35m"; // PURPLE
    public static final String CYAN_UNDERLINED = "\033[4;36m";   // CYAN
    public static final String WHITE_UNDERLINED = "\033[4;37m";  // WHITE

    // Background
    public static final String BLACK_BACKGROUND = "\033[40m";  // BLACK
    public static final String RED_BACKGROUND = "\033[41m";    // RED
    public static final String GREEN_BACKGROUND = "\033[42m";  // GREEN
    public static final String YELLOW_BACKGROUND = "\033[43m"; // YELLOW
    public static final String BLUE_BACKGROUND = "\033[44m";   // BLUE
    public static final String PURPLE_BACKGROUND = "\033[45m"; // PURPLE
    public static final String CYAN_BACKGROUND = "\033[46m";   // CYAN
    public static final String WHITE_BACKGROUND = "\033[47m";  // WHITE

    // High Intensity
    public static final String BLACK_BRIGHT = "\033[0;90m";  // BLACK
    public static final String RED_BRIGHT = "\033[0;91m";    // RED
    public static final String GREEN_BRIGHT = "\033[0;92m";  // GREEN
    public static final String YELLOW_BRIGHT = "\033[0;93m"; // YELLOW
    public static final String BLUE_BRIGHT = "\033[0;94m";   // BLUE
    public static final String PURPLE_BRIGHT = "\033[0;95m"; // PURPLE
    public static final String CYAN_BRIGHT = "\033[0;96m";   // CYAN
    public static final String WHITE_BRIGHT = "\033[0;97m";  // WHITE

    // Bold High Intensity
    public static final String BLACK_BOLD_BRIGHT = "\033[1;90m"; // BLACK
    public static final String RED_BOLD_BRIGHT = "\033[1;91m";   // RED
    public static final String GREEN_BOLD_BRIGHT = "\033[1;92m"; // GREEN
    public static final String YELLOW_BOLD_BRIGHT = "\033[1;93m";// YELLOW
    public static final String BLUE_BOLD_BRIGHT = "\033[1;94m";  // BLUE
    public static final String PURPLE_BOLD_BRIGHT = "\033[1;95m";// PURPLE
    public static final String CYAN_BOLD_BRIGHT = "\033[1;96m";  // CYAN
    public static final String WHITE_BOLD_BRIGHT = "\033[1;97m"; // WHITE

    // High Intensity backgrounds
    public static final String BLACK_BACKGROUND_BRIGHT = "\033[0;100m";// BLACK
    public static final String RED_BACKGROUND_BRIGHT = "\033[0;101m";// RED
    public static final String GREEN_BACKGROUND_BRIGHT = "\033[0;102m";// GREEN
    public static final String YELLOW_BACKGROUND_BRIGHT = "\033[0;103m";// YELLOW
    public static final String BLUE_BACKGROUND_BRIGHT = "\033[0;104m";// BLUE
    public static final String PURPLE_BACKGROUND_BRIGHT = "\033[0;105m"; // PURPLE
    public static final String CYAN_BACKGROUND_BRIGHT = "\033[0;106m";  // CYAN
    public static final String WHITE_BACKGROUND_BRIGHT = "\033[0;107m";   // WHITE

    public static final String	SANE				= "\u001B[0m";

    public static final String	HIGH_INTENSITY		= "\u001B[1m";
    public static final String	LOW_INTENSITY		= "\u001B[2m";

    public static final String	ITALIC				= "\u001B[3m";
    public static final String	UNDERLINE			= "\u001B[4m";
    public static final String	BLINK				= "\u001B[5m";
    public static final String	RAPID_BLINK			= "\u001B[6m";
    public static final String	REVERSE_VIDEO		= "\u001B[7m";
    public static final String	INVISIBLE_TEXT		= "\u001B[8m";

    public static final String EMPTY_RES = "   ";
    public static final String WHITE_MARBLE = WHITE_BRIGHT + MARBLE.getAsciiArt() + RESET;
    public static final String RED_MARBLE = RED + MARBLE.getAsciiArt() + RESET;
    public static final String PURPLE_MARBLE = PURPLE + MARBLE.getAsciiArt() + RESET;
    public static final String YELLOW_MARBLE = YELLOW + MARBLE.getAsciiArt() + RESET;
    public static final String BLUE_MARBLE = BLUE + MARBLE.getAsciiArt() + RESET;
    public static final String GRAY_MARBLE = BLACK_BRIGHT + MARBLE.getAsciiArt() + RESET;

    AnsiEnum(final String asciiArt) {
        this.asciiArt = asciiArt;
    }

    public static String colorString(String toColor, String color) {
        color = color.toUpperCase(Locale.ROOT);
        switch (color) {
            case "GREEN":
                return AnsiEnum.GREEN_BRIGHT + toColor + AnsiEnum.RESET;
            case "PURPLE":
                return AnsiEnum.PURPLE + toColor + AnsiEnum.RESET;
            case "YELLOW":
                return AnsiEnum.YELLOW_BRIGHT + toColor + AnsiEnum.RESET;
            case "BLUE":
                return AnsiEnum.BLUE_BRIGHT + toColor + AnsiEnum.RESET;
            case "GRAY":
                return AnsiEnum.BLACK_BRIGHT + toColor + AnsiEnum.RESET;
            case "RED":
                return AnsiEnum.RED_BRIGHT + toColor + AnsiEnum.RESET;
            default:
                return toColor;
        }
    }

    public static int getStringLengthWithoutANSI(String str) {
        return str.replaceAll("(\\x9B|\\x1B\\[)[0-?]*[ -/]*[@-~]", "").length();
    }

    public String getAsciiArt() {
        return asciiArt;
    }

}

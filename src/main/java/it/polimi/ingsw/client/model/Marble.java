package it.polimi.ingsw.client.model;

import it.polimi.ingsw.client.view.cli.AnsiUtilities;
import it.polimi.ingsw.client.view.cli.Printable;

/**
 * This class represents a Marble presented in the Game
 */
public class Marble extends Printable {
    private final String color;
    private final static String EMPTY_RES_ID = "EMPTY_RES";

    /**
     * Create a new Marble by specifying the color.
     *
     * @param color Color of the Marble
     */
    public Marble(String color) {
        this.color = color;
    }

    /**
     * Get method that return the ID of the empty resource
     *
     * @return the ID of the empty resource
     */
    public static String getEmptyResId() {
        return EMPTY_RES_ID;
    }

    /**
     * Get method that return the color of the Marble
     *
     * @return the color of the Marble
     */
    public String getColor() {
        return color;
    }

    /**
     * Get method that return the ENUM color
     *
     * @param color is the String color in input
     * @return the ENUM color
     */
    public static String getPrintable(String color) {
        switch (color) {
            case "WHITE":
                return AnsiUtilities.WHITE_MARBLE;
            case "RED":
                return AnsiUtilities.RED_MARBLE;
            case "PURPLE":
                return AnsiUtilities.PURPLE_MARBLE;
            case "YELLOW":
                return AnsiUtilities.YELLOW_MARBLE;
            case "BLUE":
                return AnsiUtilities.BLUE_MARBLE;
            case "GRAY":
                return AnsiUtilities.GRAY_MARBLE;
            default:
                return AnsiUtilities.EMPTY_RES;
        }
    }

    @Override
    public int getWidth() {
        return 1;
    }
}

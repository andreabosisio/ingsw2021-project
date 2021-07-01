package it.polimi.ingsw.client.model;

import it.polimi.ingsw.client.view.cli.AnsiUtilities;
import it.polimi.ingsw.client.view.cli.Printable;


public class Marble extends Printable {
    private final String color;
    private final static String EMPTY_RES_ID = "EMPTY_RES";

    public static String getEmptyResId() {
        return EMPTY_RES_ID;
    }

    public Marble(String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }

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

package it.polimi.ingsw.client.model;

import it.polimi.ingsw.client.view.cli.AsciiArts;

public class Marble {
    private final String color;

    public Marble(String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }

    public static String getPrintable(String color) {
        switch (color) {
            case "WHITE":
               return AsciiArts.WHITE_MARBLE;
            case "RED":
                return AsciiArts.RED_MARBLE;
            case "PURPLE":
                return AsciiArts.PURPLE_MARBLE;
            case "YELLOW":
                return AsciiArts.YELLOW_MARBLE;
            case "BLUE":
                return AsciiArts.BLUE_MARBLE;
            case "GRAY":
                return AsciiArts.GRAY_MARBLE;
            default:
                return AsciiArts.EMPTY_RES;
        }
    }
}

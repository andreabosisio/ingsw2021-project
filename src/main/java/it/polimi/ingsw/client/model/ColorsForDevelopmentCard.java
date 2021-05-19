package it.polimi.ingsw.client.model;

import it.polimi.ingsw.client.view.cli.AsciiArts;

public class ColorsForDevelopmentCard {
    public static String getAsciiDevCardByColor(String color) {
        switch (color) {
            case "GREEN":
                return AsciiArts.GREEN_BRIGHT;
            case "PURPLE":
                return AsciiArts.PURPLE;
            case "YELLOW":
                return AsciiArts.YELLOW_BRIGHT;
            case "BLUE":
                return AsciiArts.BLUE_BRIGHT;
            case "GRAY":
                return AsciiArts.BLACK_BRIGHT;
            case "RED":
                return AsciiArts.RED_BRIGHT;
            default:
                return AsciiArts.INVISIBLE_TEXT;
        }
    }
}

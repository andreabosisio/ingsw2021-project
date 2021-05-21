package it.polimi.ingsw.client.model;

import it.polimi.ingsw.client.view.cli.AnsiEnum;

public class ColorsForCards {
    //todo remove
    public static String getAsciiDevCardByColor(String color) {
        switch (color) {
            case "GREEN":
                return AnsiEnum.GREEN_BRIGHT;
            case "PURPLE":
                return AnsiEnum.PURPLE;
            case "YELLOW":
                return AnsiEnum.YELLOW_BRIGHT;
            case "BLUE":
                return AnsiEnum.BLUE_BRIGHT;
            case "GRAY":
                return AnsiEnum.BLACK_BRIGHT;
            case "RED":
                return AnsiEnum.RED_BRIGHT;
            default:
                return AnsiEnum.INVISIBLE_TEXT;
        }
    }
}

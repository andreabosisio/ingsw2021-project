package it.polimi.ingsw.client.model;

import it.polimi.ingsw.client.view.cli.AnsiEnum;
import it.polimi.ingsw.client.view.cli.Printable;


public class Marble extends Printable {
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
               return AnsiEnum.WHITE_MARBLE;
            case "RED":
                return AnsiEnum.RED_MARBLE;
            case "PURPLE":
                return AnsiEnum.PURPLE_MARBLE;
            case "YELLOW":
                return AnsiEnum.YELLOW_MARBLE;
            case "BLUE":
                return AnsiEnum.BLUE_MARBLE;
            case "GRAY":
                return AnsiEnum.GRAY_MARBLE;
            case "EMPTY_RES":
                return AnsiEnum.EMPTY_RES;
            default:
                return AnsiEnum.EMPTY_RES;
        }
    }

    @Override
    public int getWidth() {
        return 1;
    }

}

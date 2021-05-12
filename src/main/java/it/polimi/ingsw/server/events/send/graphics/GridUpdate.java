package it.polimi.ingsw.server.events.send.graphics;

import it.polimi.ingsw.server.model.enums.CardColorEnum;
import it.polimi.ingsw.server.model.gameBoard.GameBoard;

public class GridUpdate extends GraphicsUpdateEvent{
    private final int level;
    private final String color;
    private String iD;

    public GridUpdate(CardColorEnum color, int level) {
        super("grid");
        this.level = level;
        this.color = color.toString();
        //todo: togli il try catch e metti return null (getCardByColorAndLevel) e iD final
        try {
            this.iD = GameBoard.getGameBoard().getDevelopmentCardsGrid().getCardByColorAndLevel(color, level).getID();
        } catch (IndexOutOfBoundsException e) {
            this.iD = "empty";
        }
    }
}

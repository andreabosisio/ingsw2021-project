package it.polimi.ingsw.server.events.send.graphics;

import it.polimi.ingsw.server.model.enums.CardColorEnum;
import it.polimi.ingsw.server.model.gameBoard.GameBoard;

import java.util.ArrayList;
import java.util.List;

public class GridUpdate {
    private final Integer level;
    private final String color;
    private final String iD;
    private final List<String> fullGrid;

    public GridUpdate(CardColorEnum color, int level) {
        this.level = level;
        this.color = color.toString();
        this.iD = GameBoard.getGameBoard().getDevelopmentCardsGrid().getCardByColorAndLevel(color, level).getID();
        this.fullGrid = null;
    }

    public GridUpdate() {
        this.level = null;
        this.color = null;
        this.iD = null;
        this.fullGrid = new ArrayList<>(GameBoard.getGameBoard().getDevelopmentCardsGrid().getFullGrid());
    }
}

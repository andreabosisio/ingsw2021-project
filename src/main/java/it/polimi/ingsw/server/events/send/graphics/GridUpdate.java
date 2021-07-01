package it.polimi.ingsw.server.events.send.graphics;

import it.polimi.ingsw.commons.enums.CardColorsEnum;
import it.polimi.ingsw.server.model.gameBoard.GameBoard;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains the data for an update of the Development Cards Grid.
 */
public class GridUpdate {
    private final Integer level;
    private final String color;
    private final String iD;
    private final List<String> fullGrid;

    /**
     * Create a new iterative Grid Update.
     *
     * @param color Color of the new Card
     * @param level Level of the new Card
     */
    public GridUpdate(CardColorsEnum color, int level) {
        this.level = level;
        this.color = color.toString();
        this.iD = GameBoard.getGameBoard().getDevelopmentCardsGrid().getCardByColorAndLevel(color, level).getID();
        this.fullGrid = null;
    }

    /**
     * Create a new full Grid Update containing all the cards.
     */
    public GridUpdate() {
        this.level = null;
        this.color = null;
        this.iD = null;
        this.fullGrid = new ArrayList<>(GameBoard.getGameBoard().getDevelopmentCardsGrid().getFullGrid());
    }
}

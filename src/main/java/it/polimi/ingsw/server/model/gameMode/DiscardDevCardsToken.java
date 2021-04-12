package it.polimi.ingsw.server.model.gameMode;

import it.polimi.ingsw.server.model.enums.CardColorEnum;
import it.polimi.ingsw.server.model.gameBoard.DevelopmentCardsGrid;
import it.polimi.ingsw.server.model.gameBoard.GameBoard;

/**
 * Class that represents the token that discards two Development Cards
 * of the color indicated.
 */
public class DiscardDevCardsToken implements SoloActionToken {
    private final CardColorEnum color;

    public DiscardDevCardsToken(CardColorEnum color) {
        this.color = color;
    }

    /**
     * This method implements the action of the Token
     *
     * @param gameBoard is not used
     * @param lorenzo   is not used
     * @return false
     */
    @Override
    public boolean doAction(GameBoard gameBoard, Lorenzo lorenzo) {
        DevelopmentCardsGrid.getDevelopmentCardsGrid().removeCardByColor(this.color);
        DevelopmentCardsGrid.getDevelopmentCardsGrid().removeCardByColor(this.color);
        return false;
    }

    /**
     * Get method used for testing
     *
     * @return the color of the Development Card to discard
     */
    public CardColorEnum getColor() {
        return color;
    }
}
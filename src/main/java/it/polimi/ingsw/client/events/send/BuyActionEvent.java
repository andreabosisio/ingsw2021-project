package it.polimi.ingsw.client.events.send;

import it.polimi.ingsw.client.utils.ClientParser;

import java.util.List;

/**
 * Represent the request of a Buy Action by a Player.
 */
public class BuyActionEvent extends EventToServer {
    private final String cardColor;
    private final int cardLevel;
    private final List<Integer> resourcePositions;

    /**
     * Create a new Buy Action Event by defining the card's color, the card's level and
     * the positions of the resources used to buy the desired card.
     *
     * @param cardColor         Color of the desired card
     * @param cardLevel         Level of the desired card
     * @param resourcePositions Positions of the resources used to buy the desired card
     */
    public BuyActionEvent(String cardColor, int cardLevel, List<Integer> resourcePositions) {
        super(ClientParser.BUY_ACTION_TYPE);
        this.cardColor = cardColor;
        this.cardLevel = cardLevel;
        this.resourcePositions = resourcePositions;
    }
}

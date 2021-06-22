package it.polimi.ingsw.server.events.receive;

import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.server.model.ModelInterface;

import java.util.List;

/**
 * Represent the request of a Buy Action by a Player (from the Client).
 */
public class BuyEvent extends ReceiveEvent {
    private final String cardColor;
    private final int cardLevel;
    private final List<Integer> resourcePositions;

    /**
     * Set the needed data to buy a card.
     *
     * @param sender The nickname of the buyer
     * @param cardColor Color of the desired card
     * @param cardLevel Level of the desired card
     * @param resourcePositions Positions of the resources used to buy the desired card
     */
    public BuyEvent(String sender, String cardColor, int cardLevel, List<Integer> resourcePositions) {
        super(sender);
        this.cardColor = cardColor;
        this.cardLevel = cardLevel;
        this.resourcePositions = resourcePositions;
    }

    /**
     * Executes the Buy Action.
     *
     * @param modelInterface The model reference
     * @return true if the card has been successfully bought
     * @throws InvalidEventException      if the player can't buy the card
     * @throws InvalidIndexException      if one of the resource positions is negative
     * @throws EmptySlotException         if one of the resource slots is empty
     * @throws NonAccessibleSlotException if one of the resource position represents a slot that's not accessible
     */
    @Override
    public boolean doAction(ModelInterface modelInterface) throws InvalidIndexException, InvalidEventException, EmptySlotException, NonAccessibleSlotException {
        return modelInterface.buyAction(cardColor, cardLevel, resourcePositions);
    }
}

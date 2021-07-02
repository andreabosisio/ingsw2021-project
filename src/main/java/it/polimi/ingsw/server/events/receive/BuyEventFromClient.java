package it.polimi.ingsw.server.events.receive;

import it.polimi.ingsw.server.exceptions.EmptySlotException;
import it.polimi.ingsw.server.exceptions.InvalidEventException;
import it.polimi.ingsw.server.exceptions.InvalidIndexException;
import it.polimi.ingsw.server.exceptions.NonAccessibleSlotException;
import it.polimi.ingsw.server.model.ModelInterface;
import it.polimi.ingsw.server.utils.ServerParser;

import java.util.List;

/**
 * Represent the request of a Buy Action by a Player (from the Client).
 */
public class BuyEventFromClient extends EventFromClient {
    private final String cardColor;
    private final int cardLevel;
    private final List<Integer> resourcePositions;

    /**
     * Set the needed data to buy a card.
     *
     * @param sender            The nickname of the buyer
     * @param cardColor         Color of the desired card
     * @param cardLevel         Level of the desired card
     * @param resourcePositions Positions of the resources used to buy the desired card
     */
    public BuyEventFromClient(String sender, String cardColor, int cardLevel, List<Integer> resourcePositions) {
        super(sender, ServerParser.BUY_ACTION_TYPE);
        this.cardColor = cardColor;
        this.cardLevel = cardLevel;
        this.resourcePositions = resourcePositions;
    }

    /**
     * Executes the action.
     *
     * @param modelInterface The reference of the game model
     * @return true if the action has been correctly executed
     * @throws EmptySlotException           if an empty resource slot has been accessed
     * @throws NonAccessibleSlotException   if a non accessible resource slot has been accessed
     * @throws InvalidEventException        if it's an invalid action
     * @throws InvalidIndexException        if an index it's out of range
     */
    @Override
    public boolean doAction(ModelInterface modelInterface) throws InvalidIndexException, InvalidEventException, EmptySlotException, NonAccessibleSlotException {
        return modelInterface.buyAction(cardColor, cardLevel, resourcePositions);
    }
}

package it.polimi.ingsw.server.events.receive;

import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.server.model.ModelInterface;

import java.util.List;

public class BuyEvent extends ReceiveEvent {
    private final String cardColor;
    private final int cardLevel;
    private final List<Integer> resourcePositions;
    public BuyEvent(String sender, String cardColor, int cardLevel, List<Integer> resourcePositions) {
        super(sender);
        this.cardColor = cardColor;
        this.cardLevel = cardLevel;
        this.resourcePositions = resourcePositions;
    }

    @Override
    public boolean doAction(ModelInterface modelInterface) throws InvalidIndexException, InvalidEventException, EmptySlotException, NonAccessibleSlotException {
        return modelInterface.buyAction(cardColor, cardLevel, resourcePositions);
    }
}

package it.polimi.ingsw.server.events.receive;

import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.server.model.ModelInterface;

import java.util.List;

public class BuyReceiveEvent extends ReceiveEvent {
    private final String cardColor;
    private final int cardLevel;
    private final List<Integer> resourcePositions;
    public BuyReceiveEvent(String nickname, String cardColor, int cardLevel, List<Integer> resourcePositions) {
        super(nickname);
        this.cardColor=cardColor;
        this.cardLevel=cardLevel;
        this.resourcePositions = resourcePositions;
    }

    @Override
    public boolean doAction(ModelInterface modelInterface) throws InvalidIndexException, InvalidEventException, NonStorableResourceException, EmptySlotException, NonAccessibleSlotException {
        return modelInterface.buyAction(cardColor,cardLevel,resourcePositions);
    }
}

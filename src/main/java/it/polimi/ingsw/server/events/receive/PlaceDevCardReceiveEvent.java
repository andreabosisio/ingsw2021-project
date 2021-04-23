package it.polimi.ingsw.server.events.receive;

import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.server.model.ModelInterface;

public class PlaceDevCardReceiveEvent extends ReceiveEvent {
    private final int slotPosition;
    public PlaceDevCardReceiveEvent(String nickname, int slotPosition) {
        super(nickname);
        this.slotPosition = slotPosition;
    }

    @Override
    public boolean doAction(ModelInterface modelInterface) throws InvalidIndexException, InvalidEventException, NonStorableResourceException, EmptySlotException, NonAccessibleSlotException {
        return modelInterface.placeDevCardAction(slotPosition);
    }
}

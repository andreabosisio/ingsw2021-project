package it.polimi.ingsw.server.events.receive;

import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.server.model.ModelInterface;

import java.util.List;

public class PlaceResourcesReceiveEvent extends ReceiveEvent {
    private final List<Integer> placementChoices;
    public PlaceResourcesReceiveEvent(String nickname, List<Integer> placementChoices) {
        super(nickname);
        this.placementChoices = placementChoices;
    }

    @Override
    public boolean doAction(ModelInterface modelInterface) throws InvalidIndexException, InvalidEventException, NonStorableResourceException, EmptySlotException, NonAccessibleSlotException {
        return modelInterface.placeResourceAction(placementChoices);
    }
}

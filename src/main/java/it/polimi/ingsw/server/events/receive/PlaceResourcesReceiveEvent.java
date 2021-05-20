package it.polimi.ingsw.server.events.receive;

import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.server.model.ModelInterface;

import java.util.List;

public class PlaceResourcesReceiveEvent extends ReceiveEvent {
    private final List<Integer> placementChoices;
    private final boolean isFinal;
    public PlaceResourcesReceiveEvent(String nickname, List<Integer> placementChoices,boolean isFinal) {
        super(nickname);
        this.placementChoices = placementChoices;
        this.isFinal = isFinal;
    }

    @Override
    public boolean doAction(ModelInterface modelInterface) throws InvalidIndexException, InvalidEventException, EmptySlotException, NonAccessibleSlotException {
        return modelInterface.placeResourceAction(placementChoices,isFinal);
    }
}

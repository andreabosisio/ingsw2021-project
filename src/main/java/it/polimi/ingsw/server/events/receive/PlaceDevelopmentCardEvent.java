package it.polimi.ingsw.server.events.receive;

import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.server.model.ModelInterface;

public class PlaceDevelopmentCardEvent extends ReceiveEvent {
    private final int slotPosition;
    public PlaceDevelopmentCardEvent(String nickname, int slotPosition) {
        super(nickname);
        this.slotPosition = slotPosition;
    }

    @Override
    public boolean doAction(ModelInterface modelInterface) throws InvalidEventException {
        return modelInterface.placeDevelopmentCardAction(slotPosition);
    }
}

package it.polimi.ingsw.server.events.receive;

import it.polimi.ingsw.server.exceptions.*;
import it.polimi.ingsw.server.model.ModelInterface;

public class CheatEvent extends  ReceiveEvent{
    private static final String CHEAT_TYPE = "cheat";
    public CheatEvent(String sender) {
        super(sender,CHEAT_TYPE);
    }

    @Override
    public boolean doAction(ModelInterface modelInterface) throws InvalidIndexException, InvalidEventException, NonStorableResourceException, EmptySlotException, NonAccessibleSlotException, InvalidSetupException {
        modelInterface.cheat();
        return super.doAction(modelInterface);
    }
}

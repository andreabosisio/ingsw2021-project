package it.polimi.ingsw.server.events.receive;

import it.polimi.ingsw.server.exceptions.*;
import it.polimi.ingsw.server.model.ModelInterface;
import it.polimi.ingsw.server.utils.ServerParser;

public class CheatEventFromClient extends EventFromClient {
    public CheatEventFromClient(String sender) {
        super(sender, ServerParser.cheatType);
    }

    @Override
    public boolean doAction(ModelInterface modelInterface) throws InvalidIndexException, InvalidEventException, NonStorableResourceException, EmptySlotException, NonAccessibleSlotException, InvalidSetupException {
        modelInterface.cheat();
        return super.doAction(modelInterface);
    }
}

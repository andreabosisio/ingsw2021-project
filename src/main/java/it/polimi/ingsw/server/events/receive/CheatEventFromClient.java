package it.polimi.ingsw.server.events.receive;

import it.polimi.ingsw.server.exceptions.*;
import it.polimi.ingsw.server.model.ModelInterface;
import it.polimi.ingsw.server.utils.ServerParser;

/**
 * Request to cheat.
 */
public class CheatEventFromClient extends EventFromClient {

    /**
     * Create a new request to cheat by the Server Admin.
     *
     * @param sender The Server Admin
     */
    public CheatEventFromClient(String sender) {
        super(sender, ServerParser.CHEAT_TYPE);
    }

    @Override
    public boolean doAction(ModelInterface modelInterface) throws InvalidIndexException, InvalidEventException, NonStorableResourceException, EmptySlotException, NonAccessibleSlotException, InvalidSetupException {
        modelInterface.cheat();
        return super.doAction(modelInterface);
    }
}

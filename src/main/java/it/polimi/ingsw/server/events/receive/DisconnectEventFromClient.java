package it.polimi.ingsw.server.events.receive;

import it.polimi.ingsw.server.exceptions.*;
import it.polimi.ingsw.server.model.ModelInterface;
import it.polimi.ingsw.server.utils.ServerParser;

/**
 * Represent the request of disconnection from the Client
 */
public class DisconnectEventFromClient extends EventFromClient {
    /**
     * Create a new Event by giving the sender of the action request.
     *
     * @param sender The nickname of the Player who wants to perform an action
     */
    public DisconnectEventFromClient(String sender) {
        super(sender, ServerParser.DISCONNECT_TYPE);
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
     * @throws InvalidSetupException        if it's an invalid setup event
     * @throws NonStorableResourceException if a Non Storable Resource has been requested
     */
    @Override
    public boolean doAction(ModelInterface modelInterface) throws InvalidIndexException, InvalidEventException, NonStorableResourceException, EmptySlotException, NonAccessibleSlotException, InvalidSetupException {
        modelInterface.disconnectPlayer(getNickname());
        return super.doAction(modelInterface);
    }
}

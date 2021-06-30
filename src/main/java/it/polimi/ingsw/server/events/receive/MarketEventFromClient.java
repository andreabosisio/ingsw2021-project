package it.polimi.ingsw.server.events.receive;

import it.polimi.ingsw.server.exceptions.*;
import it.polimi.ingsw.server.model.ModelInterface;

/**
 * Represent the request of a Market Action by a Player (from the Client).
 */
public class MarketEventFromClient extends EventFromClient {
    private final int arrowID;

    /**
     * Create a new Market Action request by specifying the arrow's ID of the selected Market line.
     *
     * @param nickname of the Player who wants to perform aa Market Action
     * @param arrowID The arrow's ID of the selected Market line
     */
    public MarketEventFromClient(String nickname, int arrowID) {
        super(nickname);
        this.arrowID = arrowID;
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
        return modelInterface.marketAction(arrowID);
    }
}

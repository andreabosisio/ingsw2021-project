package it.polimi.ingsw.server.events.receive;

import it.polimi.ingsw.server.exceptions.*;
import it.polimi.ingsw.server.model.ModelInterface;
import it.polimi.ingsw.server.utils.ServerParser;

/**
 * Represent the request of a new Card Placement by a Player (from the Client).
 */
public class PlaceDevelopmentCardEventFromClient extends EventFromClient {
    private final int slotPosition;

    /**
     * Create a new Card Placement request by specifying the Production Slot position.
     *
     * @param nickname     of the Player who wants to place a new card
     * @param slotPosition The index of the chosen Production Slot
     */
    public PlaceDevelopmentCardEventFromClient(String nickname, int slotPosition) {
        super(nickname, ServerParser.cardPlacementActionType);
        this.slotPosition = slotPosition;
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
        return modelInterface.placeDevelopmentCardAction(slotPosition);
    }
}

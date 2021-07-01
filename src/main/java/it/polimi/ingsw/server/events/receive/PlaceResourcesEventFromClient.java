package it.polimi.ingsw.server.events.receive;

import it.polimi.ingsw.server.exceptions.*;
import it.polimi.ingsw.server.model.ModelInterface;
import it.polimi.ingsw.server.utils.ServerParser;

import java.util.List;

/**
 * Represent the request of a Warehouse reordering by a Player (from the Client).
 */
public class PlaceResourcesEventFromClient extends EventFromClient {
    private final List<Integer> placementChoices;
    private final boolean isFinal;

    /**
     * Create a new Resource Placement request.
     *
     * @param nickname         of the Player who wants to reorder the Warehouse
     * @param placementChoices A List containing all the Resource's position swaps
     * @param isFinal          true if it's the final Warehouse reordering configuration
     */
    public PlaceResourcesEventFromClient(String nickname, List<Integer> placementChoices, boolean isFinal) {
        super(nickname, ServerParser.resourcesPlacementActionType);
        this.placementChoices = placementChoices;
        this.isFinal = isFinal;
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
        return modelInterface.placeResourceAction(placementChoices, isFinal);
    }
}

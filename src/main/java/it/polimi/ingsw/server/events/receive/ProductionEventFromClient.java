package it.polimi.ingsw.server.events.receive;

import it.polimi.ingsw.server.exceptions.*;
import it.polimi.ingsw.server.model.ModelInterface;
import it.polimi.ingsw.server.utils.ServerParser;

import java.util.List;
import java.util.Map;

/**
 * Represent the request of a Production Action by a Player (from the Client).
 */
public class ProductionEventFromClient extends EventFromClient {
    private final Map<Integer, List<Integer>> inResourcesForEachProductions;
    private final Map<Integer, String> outResourcesForEachProductions;

    /**
     * Create a new Production Event.
     *
     * @param nickname                       of the Player who want to perform a Production Action
     * @param inResourcesForEachProductions  A map containing the indexes of the chosen Resources to perform the Production of the chosen Production Slot index which is the key.
     * @param outResourcesForEachProductions A map containing the colors of the desired Resources as result of the Production of the chosen Production Slot index which is the key.
     */
    public ProductionEventFromClient(String nickname, Map<Integer, List<Integer>> inResourcesForEachProductions, Map<Integer, String> outResourcesForEachProductions) {
        super(nickname, ServerParser.PRODUCTION_ACTION_TYPE);
        this.inResourcesForEachProductions = inResourcesForEachProductions;
        this.outResourcesForEachProductions = outResourcesForEachProductions;
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
     * @throws NonStorableResourceException if a Non Storable Resource has been requested
     */
    @Override
    public boolean doAction(ModelInterface modelInterface) throws InvalidIndexException, InvalidEventException, NonStorableResourceException, EmptySlotException, NonAccessibleSlotException {
        return modelInterface.productionAction(inResourcesForEachProductions, outResourcesForEachProductions);
    }
}

package it.polimi.ingsw.server.events.receive;

import it.polimi.ingsw.server.exceptions.InvalidEventException;
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
        super(nickname, ServerParser.CARD_PLACEMENT_ACTION_TYPE);
        this.slotPosition = slotPosition;
    }

    /**
     * Executes the action.
     *
     * @param modelInterface The reference of the game model
     * @return true if the action has been correctly executed
     * @throws InvalidEventException        if it's an invalid action
     */
    @Override
    public boolean doAction(ModelInterface modelInterface) throws InvalidEventException {
        return modelInterface.placeDevelopmentCardAction(slotPosition);
    }
}

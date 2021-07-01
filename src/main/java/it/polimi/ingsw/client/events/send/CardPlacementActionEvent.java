package it.polimi.ingsw.client.events.send;

import it.polimi.ingsw.client.utils.ClientParser;

/**
 * Represent the request of the Placement of a new card by a Player.
 */
public class CardPlacementActionEvent extends EventToServer {
    private final int slotPosition;

    /**
     * Represent the request of the Placement of a new card by a Player in a Production Slot.
     *
     * @param slotPosition The index of the Production Slot where the card should be placed
     */
    public CardPlacementActionEvent(int slotPosition) {
        super(ClientParser.CARD_PLACEMENT_ACTION_TYPE);
        this.slotPosition = slotPosition;
    }
}

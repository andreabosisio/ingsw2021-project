package it.polimi.ingsw.server.model.cards;

import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.server.model.resources.Resource;

import java.util.List;

/**
 * Implements the Leader Card with the ability to give two more Slots for the Warehouse
 */
public class WarehouseLeaderCard extends LeaderCard {

    private final Resource extraSlotsType;

    /**
     * Used to construct a LeaderCard of type warehouse
     *
     * @param ID             id of the leaderCard
     * @param points         points the card is worth
     * @param requirements   requirements to activate the card
     * @param extraSlotsType color of the resource placeable on the card
     */
    public WarehouseLeaderCard(String ID, int points, List<Requirement> requirements, Resource extraSlotsType) {
        super(ID, points, requirements);
        this.extraSlotsType = extraSlotsType;
    }

    /**
     * Activate the LeaderCard for the player and (only after) add it to personalBoard list of active leaders.
     * Activate the first extra slots zone that has not yet been activated.
     *
     * @param player player owner of the card
     * @return true if an extra slots zone has been activated
     */
    @Override
    public boolean activate(Player player) {
        player.getPersonalBoard().getWarehouse().addExtraSlots(this.extraSlotsType);
        return super.activate(player);
    }

}

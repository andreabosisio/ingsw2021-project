package it.polimi.ingsw.server.model.cards;

import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.server.model.resources.Resource;

import java.util.List;

/**
 * Implements the Leader Card with the ability to give a discount to buy a Development Card.
 */
public class DiscountLeaderCard extends LeaderCard {
    private final Resource discount;

    /**
     * Create a Discount Leader Card by specifying all needed parameters.
     *
     * @param ID The ID of the Card
     * @param points The Victory Points of the Card
     * @param requirements The Requirements to activate the Card
     * @param discount The discount provided by the Card in terms of Resource
     */
    public DiscountLeaderCard(String ID, int points, List<Requirement> requirements, Resource discount) {
        super(ID, points, requirements);
        this.discount = discount;
    }

    /**
     * Activate the LeaderCard for the player and (only after) add it to personalBoard list of active leaders.
     *
     * @param player player owner of the card
     * @return true if activated successfully
     */
    @Override
    public boolean activate(Player player) {
        return super.activate(player);
    }

    /**
     * Add the discount this card provide to currDiscounts
     *
     * @param currentDiscounts list where this card discount will be added
     * @return true if discount was added successfully
     */
    @Override
    public boolean applyDiscount(List<Resource> currentDiscounts) {
        return currentDiscounts.add(this.discount);
    }
}

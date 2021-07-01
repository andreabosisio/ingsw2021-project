package it.polimi.ingsw.server.model.cards;

import it.polimi.ingsw.commons.enums.ResourcesEnum;
import it.polimi.ingsw.server.model.player.Player;

/**
 * Represents the requirements to Activate a Leader Card in terms of owned Resources.
 */
public class ResourceRequirement implements Requirement {
    private final ResourcesEnum color;
    private final int quantity;

    /**
     * Create a new Resource Requirement by setting the needed parameters.
     *
     * @param color Color of the Resource
     * @param quantity Quantity of the owned Resources that must respect this Requirement
     */
    public ResourceRequirement(ResourcesEnum color, int quantity) {
        this.color = color;
        this.quantity = quantity;
    }

    /**
     * Check if the condition is satisfied by the player
     *
     * @param player player to check
     * @return true if satisfied
     */
    @Override
    public boolean isSatisfied(Player player) {
        return player.getPersonalBoard().getWarehouse().getAllResources()
                .stream().filter(res -> res.getColor() == color).count() >= quantity;
    }
}

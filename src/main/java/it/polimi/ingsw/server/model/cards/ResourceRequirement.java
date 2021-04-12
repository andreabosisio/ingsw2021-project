package it.polimi.ingsw.server.model.cards;

import it.polimi.ingsw.server.model.enums.ResourceEnum;
import it.polimi.ingsw.server.model.player.Player;

public class ResourceRequirement implements Requirement {
    private final ResourceEnum color;
    private final int quantity;


    public ResourceRequirement(ResourceEnum color, int quantity) {
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

package it.polimi.ingsw.server.model.cards;

import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.server.model.player.Warehouse;
import it.polimi.ingsw.server.model.resources.Resource;
import java.util.List;

public class WarehouseLeaderCard extends LeaderCard implements Warehouse {
    private final Resource extraSlotsType;

    public WarehouseLeaderCard(String ID, int points, List<Requirement> requirements, Resource extraSlotsType) {
        super(ID, points, requirements);
        this.extraSlotsType = extraSlotsType;
    }

    /**
     * Activate the LeaderCard for the player and !!ONLY AFTER!! add it to personalBoard list of active leaders
     *
     * @param player player owner of the card
     * @return true if activated successfully
     */
    @Override
    public boolean activate(Player player) {
        return super.activate(player);
    }
}

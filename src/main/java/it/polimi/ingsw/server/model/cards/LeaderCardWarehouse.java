package it.polimi.ingsw.server.model.cards;

import it.polimi.ingsw.server.model.player.Warehouse;
import it.polimi.ingsw.server.model.resources.Resource;
import java.util.List;

public class LeaderCardWarehouse extends LeaderCard implements Warehouse {
    private final Resource extraSlotsType;

    public LeaderCardWarehouse(String ID, int points, List<Requirement> requirements,Resource extraSlotsType) {
        super(ID, points, requirements);
        this.extraSlotsType = extraSlotsType;
    }
}

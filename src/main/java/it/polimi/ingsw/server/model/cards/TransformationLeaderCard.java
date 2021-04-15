package it.polimi.ingsw.server.model.cards;

import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.server.model.resources.Resource;

import java.util.List;

public class TransformationLeaderCard extends LeaderCard {
    private final Resource transformation;


    public TransformationLeaderCard(String ID, int points, List<Requirement> requirements, Resource transformation) {
        super(ID, points, requirements);
        this.transformation=transformation;
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

    /**
     * Check if the white resource transformation can be performed by the card
     *
     * @param resource
     * @return true if it is acceptable
     */
    @Override
    public boolean doTransformation(Resource resource) {
       return resource.addPossibleTransformations(transformation);
    }
}

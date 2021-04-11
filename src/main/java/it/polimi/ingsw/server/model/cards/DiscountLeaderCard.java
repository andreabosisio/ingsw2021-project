package it.polimi.ingsw.server.model.cards;
import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.server.model.resources.Resource;
import java.util.List;

public class DiscountLeaderCard extends LeaderCard{
    private final Resource discount;

    public DiscountLeaderCard(String ID, int points, List<Requirement> requirements, Resource discount) {
        super(ID, points, requirements);
        this.discount=discount;
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

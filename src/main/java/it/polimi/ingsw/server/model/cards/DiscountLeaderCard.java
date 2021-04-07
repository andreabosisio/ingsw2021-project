package it.polimi.ingsw.server.model.cards;
import it.polimi.ingsw.server.model.resources.Resource;
import java.util.List;

public class DiscountLeaderCard extends LeaderCard{
    private final Resource discount;

    public DiscountLeaderCard(String ID, int points, List<Requirement> requirements, Resource discount) {
        super(ID, points, requirements);
        this.discount=discount;
    }


}

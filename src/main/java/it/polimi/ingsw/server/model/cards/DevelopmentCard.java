package it.polimi.ingsw.server.model.cards;

import it.polimi.ingsw.server.model.enums.CardColor;
import it.polimi.ingsw.server.model.resources.Resource;

import java.util.List;

public class DevelopmentCard implements ProductionCard {
    private final List<Resource> inResources;
    private final List<Resource> outResources;
    private final List<Resource> price;
    private final CardColor color;
    private final int points;
    private final int level;

    public DevelopmentCard(List<Resource> inResources, List<Resource> outResources, List<Resource> price, CardColor color, int points, int level) {
        this.inResources = inResources;
        this.outResources = outResources;
        this.price = price;
        this.color = color;
        this.points = points;
        this.level = level;
    }

    @Override
    public List<Resource> usePower() {
        return null;
    }

    @Override
    public List<Resource> getInResources() {
        return null;
    }

    @Override
    public List<Resource> getOutResources() {
        return null;
    }

    @Override
    public boolean canDoProduction(List<Resource> resources) {
        return false;
    }


}

package it.polimi.ingsw.server.model.cards;

import it.polimi.ingsw.server.model.resources.Resource;

import java.util.List;

public interface ProductionCard {
    List<Resource> usePower();
    List<Resource> getInResources();
    List<Resource> getOutResources();
    boolean canDoProduction(List<Resource> resources);
}

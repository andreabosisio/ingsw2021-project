package it.polimi.ingsw.client.events.send;

import java.util.List;
import java.util.Map;

public class ProductionActionEvent extends SendEvent{
    private final Map<Integer, List<Integer>> inResourcesForEachProductions;
    private final Map<Integer, String> outResourcesForEachProductions;
    public ProductionActionEvent(Map<Integer, List<Integer>> inResourcesForEachProductions, Map<Integer, String> outResourcesForEachProductions) {
        super("productionAction");
        this.inResourcesForEachProductions = inResourcesForEachProductions;
        this.outResourcesForEachProductions = outResourcesForEachProductions;
    }
}

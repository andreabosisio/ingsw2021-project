package it.polimi.ingsw.client.events.send;

import it.polimi.ingsw.client.utils.ClientParser;

import java.util.List;
import java.util.Map;

/**
 * Represent the request of a Production Action by a Player
 */
public class ProductionActionEvent extends EventToServer {
    private final Map<Integer, List<Integer>> inResourcesForEachProductions;
    private final Map<Integer, String> outResourcesForEachProductions;

    /**
     * This event is sent when a Player chooses the input resources and the eventually output resources
     * for every Development Card to use to do the Production
     *
     * @param inResourcesForEachProductions  are the resources in input to do the Production
     * @param outResourcesForEachProductions are the resources in output to do the Production
     */
    public ProductionActionEvent(Map<Integer, List<Integer>> inResourcesForEachProductions, Map<Integer, String> outResourcesForEachProductions) {
        super(ClientParser.PRODUCTION_ACTION_TYPE);
        this.inResourcesForEachProductions = inResourcesForEachProductions;
        this.outResourcesForEachProductions = outResourcesForEachProductions;
    }
}

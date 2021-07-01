package it.polimi.ingsw.client.events.send;

import it.polimi.ingsw.client.utils.ClientParser;

import java.util.List;

/**
 * Represent the request of a Placement of the Resources into the Warehouse by a Player
 */
public class ResourcesPlacementActionEvent extends EventToServer {
    private final List<Integer> placementChoices;
    private final boolean isFinal;

    /**
     * This event is sent after a resource placement action
     *
     * @param placementChoices a List containing all the Resource's position swaps
     * @param isFinal          true if it's the final Warehouse reordering configuration
     */
    public ResourcesPlacementActionEvent(List<Integer> placementChoices, boolean isFinal) {
        super(ClientParser.RESOURCES_PLACEMENT_ACTION_TYPE);
        this.placementChoices = placementChoices;
        this.isFinal = isFinal;
    }
}

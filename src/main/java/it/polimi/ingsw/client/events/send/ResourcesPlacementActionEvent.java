package it.polimi.ingsw.client.events.send;

import java.util.List;

public class ResourcesPlacementActionEvent extends SendEvent{
    private final List<Integer> placementChoices;
    private final boolean isFinal;
    public ResourcesPlacementActionEvent(List<Integer> placementChoices,boolean isFinal) {
        super("resourcesPlacementAction");
        this.placementChoices = placementChoices;
        this.isFinal = isFinal;
    }
}

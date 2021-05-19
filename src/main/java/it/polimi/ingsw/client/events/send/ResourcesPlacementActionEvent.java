package it.polimi.ingsw.client.events.send;

import java.util.List;

public class ResourcesPlacementActionEvent extends SendEvent{
    private final List<Integer> placementChoices;
    public ResourcesPlacementActionEvent(List<Integer> placementChoices) {
        super("resourcesPlacementAction");
        this.placementChoices = placementChoices;
    }
}

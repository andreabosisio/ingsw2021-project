package it.polimi.ingsw.server.events.send.choice;

import it.polimi.ingsw.server.model.player.warehouse.Warehouse;

import java.util.ArrayList;
import java.util.List;

public class PlaceResourcesChoiceEvent extends ChoiceEvent {
    private final List<String> resourcesToPlace = new ArrayList<>();

    public PlaceResourcesChoiceEvent(String nickname, Warehouse warehouse) {
        super(nickname, "placeResources");
        warehouse.getResourcesFromMarket().forEach(resource -> resourcesToPlace.add(resource.getColor().toString()));
    }
}

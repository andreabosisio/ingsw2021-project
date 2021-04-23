package it.polimi.ingsw.server.events.send;

import it.polimi.ingsw.server.model.player.Warehouse;

import java.util.ArrayList;
import java.util.List;

public class PlaceResourcesSendEvent extends SendEvent{
    private final List<String> resourcesToPlace = new ArrayList<>();

    public PlaceResourcesSendEvent(String nickname, Warehouse warehouse) {
        super(nickname);
        warehouse.getResourcesFromMarket().forEach(resource -> resourcesToPlace.add(resource.getColor().toString()));
    }
}

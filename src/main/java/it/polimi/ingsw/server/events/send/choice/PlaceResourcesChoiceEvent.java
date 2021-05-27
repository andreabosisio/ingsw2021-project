package it.polimi.ingsw.server.events.send.choice;

import it.polimi.ingsw.server.model.player.warehouse.Warehouse;

import java.util.ArrayList;
import java.util.List;

public class PlaceResourcesChoiceEvent extends ChoiceEvent {
    public PlaceResourcesChoiceEvent(String nickname) {
        super(nickname, "placeResources");
    }
}

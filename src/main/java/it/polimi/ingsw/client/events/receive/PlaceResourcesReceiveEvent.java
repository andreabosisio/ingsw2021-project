package it.polimi.ingsw.client.events.receive;

import it.polimi.ingsw.client.view.View;

import java.util.ArrayList;
import java.util.List;

public class PlaceResourcesReceiveEvent implements ReceiveEvent{

    @Override
    public void updateView(View view) {
        view.setOnPlaceResources();
    }
}

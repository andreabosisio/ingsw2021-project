package it.polimi.ingsw.client.events.receive;

import it.polimi.ingsw.client.view.View;

/**
 * Represent a request of a placement of the Resources taken from the Market
 */
public class PlaceResourcesReceiveEvent implements EventFromServer {

    @Override
    public void updateView(View view) {
        view.setOnResourcesPlacement();
    }
}

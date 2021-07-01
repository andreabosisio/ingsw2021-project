package it.polimi.ingsw.client.events.receive;

import it.polimi.ingsw.client.view.View;

/**
 * Represent a request of a placement of a Development Card
 */
public class PlaceDevCardReceiveEvent implements EventFromServer {
    private final String newCardID;

    public PlaceDevCardReceiveEvent(String newCardID) {
        this.newCardID = newCardID;
    }

    @Override
    public void updateView(View view) {
        view.setOnDevelopmentCardPlacement(newCardID);
    }
}

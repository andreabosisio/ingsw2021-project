package it.polimi.ingsw.client.events.receive;

import it.polimi.ingsw.client.view.View;

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

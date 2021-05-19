package it.polimi.ingsw.client.events.receive;

import it.polimi.ingsw.client.view.View;

public class PlaceDevCardReceiveEvent implements ReceiveEvent{
    private final String newCardId;
    public PlaceDevCardReceiveEvent(String newCardId) {
        this.newCardId = newCardId;
    }

    @Override
    public void updateView(View view) {
        view.setOnPlaceDevCard(newCardId);
    }
}

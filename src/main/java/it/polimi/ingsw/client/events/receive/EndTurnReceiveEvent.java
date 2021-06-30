package it.polimi.ingsw.client.events.receive;

import it.polimi.ingsw.client.view.View;

/**
 * Represent the Event that set the View in End Turn
 */
public class EndTurnReceiveEvent implements EventFromServer {
    @Override
    public void updateView(View view) {
        view.setOnEndTurn();
    }
}

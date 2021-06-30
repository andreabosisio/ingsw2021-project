package it.polimi.ingsw.client.events.receive;

import it.polimi.ingsw.client.view.View;

public class EndTurnReceiveEvent implements EventFromServer {
    @Override
    public void updateView(View view) {
        view.setOnEndTurn();
    }
}

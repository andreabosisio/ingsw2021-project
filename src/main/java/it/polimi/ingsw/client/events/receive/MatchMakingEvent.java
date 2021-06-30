package it.polimi.ingsw.client.events.receive;

import it.polimi.ingsw.client.view.View;

public class MatchMakingEvent implements EventFromServer {

    @Override
    public void updateView(View view) {
        view.setOnMatchMaking();
    }
}

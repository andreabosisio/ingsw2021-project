package it.polimi.ingsw.client.events.receive;

import it.polimi.ingsw.client.view.View;

/**
 * Represent the Event of MatchMaking that set the View in the MatchMaking scene
 */
public class MatchMakingEvent implements EventFromServer {

    @Override
    public void updateView(View view) {
        view.setOnMatchMaking();
    }
}

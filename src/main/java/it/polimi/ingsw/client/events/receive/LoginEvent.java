package it.polimi.ingsw.client.events.receive;

import it.polimi.ingsw.client.view.View;

/**
 * Represent the Event of Login that set the View in the Login scene
 */
public class LoginEvent implements EventFromServer {

    @Override
    public void updateView(View view) {
        view.setOnLogin();
    }
}

package it.polimi.ingsw.client.events.receive;

import it.polimi.ingsw.client.view.View;

public class LoginEvent implements EventFromServer {

    @Override
    public void updateView(View view) {
        view.setOnLogin();
    }
}

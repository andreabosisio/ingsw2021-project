package it.polimi.ingsw.client.events.receive;

import it.polimi.ingsw.client.view.View;

public class ConnectionEvent implements ReceiveEvent {

    @Override
    public void updateView(View view) {
        view.setOnLogin();
    }
}

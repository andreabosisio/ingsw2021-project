package it.polimi.ingsw.client.events.receive;

import it.polimi.ingsw.client.view.View;

/**
 * Represent and Info message
 */
public class InfoMessageEvent implements EventFromServer {
    private final String payload;

    public InfoMessageEvent(String payload) {
        this.payload = payload;
    }

    @Override
    public void updateView(View view) {
        view.printInfoMessage(payload);
    }
}

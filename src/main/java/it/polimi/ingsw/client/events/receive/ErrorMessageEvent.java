package it.polimi.ingsw.client.events.receive;

import it.polimi.ingsw.client.view.View;

public class ErrorMessageEvent implements EventFromServer {
    private final String payload;

    public ErrorMessageEvent(String error) {
        this.payload = error;
    }

    @Override
    public void updateView(View view) {
        view.printErrorMessage(payload);
    }
}

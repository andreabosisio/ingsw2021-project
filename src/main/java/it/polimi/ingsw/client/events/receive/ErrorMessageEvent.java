package it.polimi.ingsw.client.events.receive;

import it.polimi.ingsw.client.view.View;

public class ErrorMessageEvent implements ReceiveEvent{
    private final String payload;
    public View view;

    public ErrorMessageEvent(String error) {
        this.payload = error;
    }

    @Override
    public void updateView(View view) {
        this.view = view;
        run();
    }

    @Override
    public void run() {
        view.printErrorMessage(payload);
    }
}

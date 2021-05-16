package it.polimi.ingsw.client.events.receive;

import it.polimi.ingsw.client.view.View;

public class InfoMessageEvent implements ReceiveEvent{
    private final String payload;
    private View view;

    public InfoMessageEvent(String payload) {
        this.payload = payload;
    }

    @Override
    public void updateView(View view) {
        this.view = view;
        run();
    }

    @Override
    public void run() {
        view.printInfoMessage(payload);
    }
}
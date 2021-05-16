package it.polimi.ingsw.client.events.receive;

import it.polimi.ingsw.client.view.View;

public class ChooseNumberPlayersEvent implements ReceiveEvent{

    private final String payload;
    private View view;

    public ChooseNumberPlayersEvent(String payload) {
        this.payload = payload;
    }

    @Override
    public void updateView(View view) {
        this.view = view;
        this.run();
    }

    @Override
    public void run() {
        view.setOnChooseNumberOfPlayers(payload);
    }
}

package it.polimi.ingsw.client.events.receive;

import it.polimi.ingsw.client.view.View;

import java.util.List;

public class ChooseSetupEvent implements ReceiveEvent{
    private List<String> leaderCardsIDs;
    private int numberOfResources;
    private View view;

    @Override
    public void updateView(View view) {
        this.view = view;
        run();
    }

    @Override
    public void run() {
        view.setOnSetup(leaderCardsIDs, numberOfResources);
    }
}

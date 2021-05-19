package it.polimi.ingsw.client.events.receive;

import it.polimi.ingsw.client.view.View;

import java.util.List;

public class ChooseSetupEvent implements ReceiveEvent {
    private List<String> leaderCardsIDs;
    private int numberOfResources;

    @Override
    public void updateView(View view) {
        view.setOnSetup(leaderCardsIDs, numberOfResources);
    }
}

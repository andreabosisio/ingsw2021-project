package it.polimi.ingsw.client.events;

import java.util.List;

public class ChooseSetupEvent {
    private List<String> leaderCardsIDs;
    private int numberOfResources;

    public ChooseSetupEvent(List<String> leaderCardsIDs, int numberOfResources) {
        this.leaderCardsIDs = leaderCardsIDs;
        this.numberOfResources = numberOfResources;
    }
}

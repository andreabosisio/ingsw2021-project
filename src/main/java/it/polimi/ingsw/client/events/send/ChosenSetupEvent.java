package it.polimi.ingsw.client.events.send;

import java.util.List;

public class ChosenSetupEvent extends SendEvent{

    private final List<Integer> chosenLeaderCardIndexes;
    private final List<String> chosenResources;

    public ChosenSetupEvent(List<Integer> chosenLeaderCardIndexes, List<String> chosenResources) {
        super( "setupAction");
        this.chosenLeaderCardIndexes = chosenLeaderCardIndexes;
        this.chosenResources = chosenResources;
    }
}

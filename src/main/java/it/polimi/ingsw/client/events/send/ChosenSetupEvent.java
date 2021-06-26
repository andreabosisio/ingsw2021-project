package it.polimi.ingsw.client.events.send;

import java.util.List;

public class ChosenSetupEvent extends SendEvent{

    private final List<Integer> chosenLeaderCardIndexes;
    private final List<String> chosenResources;

    /**
     * This event is sent when the player has done his setup action
     *
     * @param chosenLeaderCardIndexes indexes of the leaderCard chosen
     * @param chosenResources starting resources chosen
     */
    public ChosenSetupEvent(List<Integer> chosenLeaderCardIndexes, List<String> chosenResources) {
        super( "setupAction");
        this.chosenLeaderCardIndexes = chosenLeaderCardIndexes;
        this.chosenResources = chosenResources;
    }
}

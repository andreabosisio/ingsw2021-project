package it.polimi.ingsw.client.events.send;

import it.polimi.ingsw.client.utils.ClientParser;

import java.util.List;

/**
 * Represent the request of a first Setup Action by a Player.
 */
public class ChosenSetupEvent extends EventToServer {

    private final List<Integer> chosenLeaderCardIndexes;
    private final List<String> chosenResources;

    /**
     * This event is sent when the player has done his setup action
     *
     * @param chosenLeaderCardIndexes indexes of the leaderCard chosen
     * @param chosenResources         starting resources chosen
     */
    public ChosenSetupEvent(List<Integer> chosenLeaderCardIndexes, List<String> chosenResources) {
        super(ClientParser.SETUP_ACTION_TYPE);
        this.chosenLeaderCardIndexes = chosenLeaderCardIndexes;
        this.chosenResources = chosenResources;
    }
}

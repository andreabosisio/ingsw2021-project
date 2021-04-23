package it.polimi.ingsw.server.events.send;

import it.polimi.ingsw.server.model.cards.LeaderCard;

import java.util.ArrayList;
import java.util.List;

public class SetupSendEvent extends SendEvent{
    private final List<String> leaderCardIDs = new ArrayList<>();
    private final int numberOfResources;

    public SetupSendEvent(String nickname, List<LeaderCard> leaderCards, int numberOfResources) {
        super(nickname);
        this.numberOfResources = numberOfResources;
        leaderCards.forEach(leaderCard -> leaderCardIDs.add(leaderCard.getID()));
    }

    public List<String> getLeaderCardIDs() {
        return leaderCardIDs;
    }

    public int getNumberOfResources() {
        return numberOfResources;
    }
}

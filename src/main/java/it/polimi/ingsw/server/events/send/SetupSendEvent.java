package it.polimi.ingsw.server.events.send;

import it.polimi.ingsw.server.model.cards.LeaderCard;

import java.util.ArrayList;
import java.util.List;

public class SetupSendEvent extends SendEvent{
    private final List<LeaderCard> leaderCards = new ArrayList<>();
    private final int numberOfResources;

    public SetupSendEvent(String nickname, List<LeaderCard> leaderCards, int numberOfResources) {
        super(nickname);
        this.numberOfResources = numberOfResources;
        this.leaderCards.addAll(leaderCards);
    }

    public List<LeaderCard> getLeaderCards() {
        return leaderCards;
    }

    public int getNumberOfResources() {
        return numberOfResources;
    }
}

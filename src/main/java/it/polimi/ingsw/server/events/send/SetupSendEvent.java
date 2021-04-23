package it.polimi.ingsw.server.events.send;

import it.polimi.ingsw.server.model.cards.LeaderCard;

import java.util.ArrayList;
import java.util.List;

public class SetupSendEvent extends SendEvent{
    private final List<String> iDLeaderCard = new ArrayList<>();
    private final int numberOfResources;

    public SetupSendEvent(String nickName, List<LeaderCard> leaderCards, int numberOfResources) {
        super(nickName);
        this.numberOfResources = numberOfResources;
        leaderCards.forEach(leaderCard -> iDLeaderCard.add(leaderCard.getID()));
    }

    public List<String> getiDLeaderCard() {
        return iDLeaderCard;
    }

    public int getNumberOfResources() {
        return numberOfResources;
    }
}

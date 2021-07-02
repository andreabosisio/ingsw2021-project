package it.polimi.ingsw.server.events.send.choice;

import it.polimi.ingsw.server.model.cards.LeaderCard;
import it.polimi.ingsw.server.utils.ServerParser;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represent a request of a Setup Action
 */
public class SetupChoiceEvent extends ChoiceEvent {
    private final transient List<LeaderCard> leaderCards = new ArrayList<>();
    private final List<String> leaderCardsIDs = new ArrayList<>();
    private final int numberOfResources;


    public SetupChoiceEvent(String nickname, List<LeaderCard> leaderCards, int numberOfResources) {
        super(nickname, ServerParser.SETUP_TYPE);
        this.numberOfResources = numberOfResources;
        this.leaderCards.addAll(leaderCards);
        this.leaderCardsIDs.addAll(leaderCards.stream().map(LeaderCard::getID).collect(Collectors.toList()));
    }

    /**
     * Return the leaderCards from which to chose for this event
     *
     * @return la list containing the leaderCards
     */
    public List<LeaderCard> getLeaderCards() {
        return leaderCards;
    }

    /**
     * Return the number of resources to chose for this event
     *
     * @return the number of resources
     */
    public int getNumberOfResources() {
        return numberOfResources;
    }

}
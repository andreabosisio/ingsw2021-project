package it.polimi.ingsw.server.events.send.choice;

import it.polimi.ingsw.server.events.send.choice.ChoiceEvent;
import it.polimi.ingsw.server.model.cards.LeaderCard;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SetupChoiceEvent extends ChoiceEvent {
    private final transient List<LeaderCard> leaderCards = new ArrayList<>();
    private final List<String> leaderCardsIDs = new ArrayList<>();
    private final int numberOfResources;


    public SetupChoiceEvent(String nickname, List<LeaderCard> leaderCards, int numberOfResources) {
        super(nickname, "setup");
        this.numberOfResources = numberOfResources;
        this.leaderCards.addAll(leaderCards);
        this.leaderCardsIDs.addAll(leaderCards.stream().map(LeaderCard::getID).collect(Collectors.toList()));
    }

    public List<LeaderCard> getLeaderCards() {
        return leaderCards;
    }

    public int getNumberOfResources() {
        return numberOfResources;
    }

    /*

    @Override
    public String toJson() {
        Map<String, Object> mapJson = new HashMap<>();
        mapJson.put("leaders", leaderCards.stream().map(LeaderCard::getID).collect(Collectors.toList()));
        mapJson.put("resources", numberOfResources);
        return getGson().toJson(mapJson);
    }

     */
}
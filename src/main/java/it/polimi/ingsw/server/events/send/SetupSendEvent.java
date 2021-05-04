package it.polimi.ingsw.server.events.send;

import com.google.gson.JsonObject;
import it.polimi.ingsw.server.model.cards.LeaderCard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SetupSendEvent extends SendEvent{
    private final transient List<LeaderCard> leaderCards = new ArrayList<>();
    private final List<String> leaderCardsIDs = new ArrayList<>();
    private final int numberOfResources;


    public SetupSendEvent(String nickname, List<LeaderCard> leaderCards, int numberOfResources) {
        super(nickname);
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
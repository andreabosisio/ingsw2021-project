package it.polimi.ingsw.server.events.send.graphics;

import it.polimi.ingsw.server.model.player.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains all the Data of the latest Leader Card of the Player
 */
public class LeaderCardSlotsUpdate implements PersonalUpdate {
    @Override
    public void addUpdate(PersonalBoardUpdate personalBoardUpdate, Player player) {
        List<String> handLeaders = new ArrayList<>();
        List<String> activeLeaders = new ArrayList<>();
        player.getLeaderHand().forEach(leaderCard -> handLeaders.add(leaderCard.getID()));
        player.getPersonalBoard().getActiveLeaderCards().forEach(leaderCard -> activeLeaders.add(leaderCard.getID()));

        personalBoardUpdate.setNickname(player.getNickname());
        personalBoardUpdate.setHandLeaders(handLeaders);
        personalBoardUpdate.setActiveLeaders(activeLeaders);
    }
}

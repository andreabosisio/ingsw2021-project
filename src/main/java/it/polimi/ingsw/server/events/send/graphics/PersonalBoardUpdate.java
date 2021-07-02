package it.polimi.ingsw.server.events.send.graphics;

import it.polimi.ingsw.server.model.player.Player;

import java.util.List;
import java.util.Map;

/**
 * Update Event of all the personal Data of the Player
 */
public class PersonalBoardUpdate {
    private String nickname;
    private List<String> handLeaders;
    private List<String> activeLeaders;
    private List<List<String>> productionBoard;
    private Map<Integer, String> warehouse;

    /**
     * Set the nickname of the owner of the personalBoard which data are saved here
     *
     * @param nickname owner of the personal board
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * Saves a list of leaderCards Ids as leader in hand in this personalBoardUpdate
     *
     * @param handLeaders IDs of the leaderCards
     */
    public void setHandLeaders(List<String> handLeaders) {
        this.handLeaders = handLeaders;
    }

    /**
     * Saves a list of leaderCards Ids as leader activated in this personalBoardUpdate
     *
     * @param activeLeaders IDs of the leaderCards
     */
    public void setActiveLeaders(List<String> activeLeaders) {
        this.activeLeaders = activeLeaders;
    }

    /**
     * This method saves all the owned developmentCards IDs on a personalBoard
     *
     * @param productionBoard representation of a player owned developmentCards
     */
    public void setProductionBoard(List<List<String>> productionBoard) {
        this.productionBoard = productionBoard;
    }

    /**
     * This method saves all the resources stored in the warehouse
     *
     * @param warehouse representation of a player warehouse
     */
    public void setWarehouse(Map<Integer, String> warehouse) {
        this.warehouse = warehouse;
    }

    public PersonalBoardUpdate(Player player, PersonalUpdate... personalUpdates) {
        for (PersonalUpdate personalUpdate : personalUpdates) {
            personalUpdate.addUpdate(this, player);
        }
    }

}
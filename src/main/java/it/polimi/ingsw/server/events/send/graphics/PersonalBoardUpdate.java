package it.polimi.ingsw.server.events.send.graphics;

import it.polimi.ingsw.server.model.player.PersonalBoard;
import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.server.model.player.warehouse.Warehouse;

import java.util.ArrayList;
import java.util.*;

public class PersonalBoardUpdate {
    private final String nickname;
    private final List<String> handLeaders;
    private final List<String> activeLeaders;
    private final List<String> productionBoard;
    private final Map<Integer, String> warehouse;

    // leader card slots update
    public PersonalBoardUpdate(Player player) {
        this.handLeaders = new ArrayList<>();
        this.activeLeaders = new ArrayList<>();
        this.productionBoard = null;
        this.warehouse = null;
        this.nickname = player.getNickname();
        player.getLeaderHand().forEach(leaderCard -> handLeaders.add(leaderCard.getID()));
        player.getPersonalBoard().getActiveLeaderCards().forEach(leaderCard -> activeLeaders.add(leaderCard.getID()));
    }

    // production slots update
    public PersonalBoardUpdate(String nickname, PersonalBoard personalBoard) {
        this.handLeaders = null;
        this.activeLeaders = null;
        this.warehouse = null;
        this.nickname = nickname;
        this.productionBoard = personalBoard.getVisibleDevelopmentCards();
    }

    // warehouse update
    public PersonalBoardUpdate(String nickname, Warehouse warehouse) {
        this.handLeaders = null;
        this.activeLeaders = null;
        this.productionBoard = null;
        this.nickname = nickname;
        warehouse.reorderStrongBox();
        this.warehouse = warehouse.getAllPositionsAndResources();
    }

}
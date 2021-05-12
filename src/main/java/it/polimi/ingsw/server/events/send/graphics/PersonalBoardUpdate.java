package it.polimi.ingsw.server.events.send.graphics;

import it.polimi.ingsw.server.model.player.PersonalBoard;
import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.server.model.player.warehouse.Warehouse;

import java.util.ArrayList;
import java.util.*;
import java.util.stream.Collectors;

public class PersonalBoardUpdate extends GraphicsUpdateEvent{
    private final String nickname;
    private final List<String> handLeaders;
    private final List<String> activeLeaders;
    private final List<String> productionBoard;
    private final Map<Integer,String> warehouse;

    // leaders card update
    public PersonalBoardUpdate(Player player) {
        super("personalBoard");
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
        super("personalBoard");
        this.handLeaders = null;
        this.activeLeaders = null;
        this.warehouse = null;
        this.nickname = nickname;
        this.productionBoard = personalBoard.getVisibleDevelopmentCards();
    }

    // warehouse update
    public PersonalBoardUpdate(String nickname, Warehouse warehouse) {
        super("personalBoard");
        this.handLeaders = null;
        this.activeLeaders = null;
        this.productionBoard = null;
        this.nickname = nickname;
        //todo: to check!
        this.warehouse = warehouse.getAllPositionsAndResources()
                .entrySet().stream().peek(p -> {
                    if (p.getValue() == null)
                        p.setValue("empty");
                }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
package it.polimi.ingsw.server.events.send.graphics;

import it.polimi.ingsw.server.model.player.PersonalBoard;
import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.server.model.player.warehouse.Warehouse;

import java.util.*;

public class PersonalBoardUpdate {
    private String nickname;
    private List<String> handLeaders;
    private List<String> activeLeaders;
    private List<List<String>> productionBoard;
    private Map<Integer, String> warehouse;

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setHandLeaders(List<String> handLeaders) {
        this.handLeaders = handLeaders;
    }

    public void setActiveLeaders(List<String> activeLeaders) {
        this.activeLeaders = activeLeaders;
    }

    public void setProductionBoard(List<List<String>> productionBoard) {
        this.productionBoard = productionBoard;
    }

    public void setWarehouse(Map<Integer, String> warehouse) {
        this.warehouse = warehouse;
    }

    public PersonalBoardUpdate(Player player, PersonalUpdate... personalUpdates) {
        for(PersonalUpdate personalUpdate : personalUpdates) {
            personalUpdate.addUpdateTo(this, player);
        }
    }

}
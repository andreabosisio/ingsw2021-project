package it.polimi.ingsw.client.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Player {
    private final String nickname;
    private List<String> handLeaders;
    private List<String> activeLeaders;
    private List<String> productionBoard;
    private Map<Integer, String> warehouse;

    public Player(String nickname) {
        this.nickname = nickname;
        this.handLeaders = new ArrayList<>();
        this.activeLeaders = new ArrayList<>();
        this.productionBoard = new ArrayList<>();
        this.warehouse = new HashMap<>();
    }

    public String getNickname() {
        return nickname;
    }

    public void setHandLeaders(List<String> handLeaders) {
        if(handLeaders!=null) {
            this.handLeaders = handLeaders;
        }
    }

    public void setActiveLeaders(List<String> activeLeaders) {
        if(activeLeaders!=null) {
            this.activeLeaders = activeLeaders;
        }
    }
    public void setProductionBoard(List<String> productionBoard) {
        if(productionBoard!=null) {
            this.productionBoard = productionBoard;
        }
    }
    public void setWarehouse(Map<Integer, String> warehouse) {
        if(warehouse!=null) {
            this.warehouse = warehouse;
        }
    }
    public List<String> getHandLeaders() {
        return handLeaders;
    }

    public void update(){
        Player player = Board.getBoard().getPlayerByNickname(nickname);
        player.setActiveLeaders(activeLeaders);
        player.setWarehouse(warehouse);
        player.setProductionBoard(productionBoard);
        player.setHandLeaders(handLeaders);

    }
}

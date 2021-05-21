package it.polimi.ingsw.client.model;

import it.polimi.ingsw.client.view.cli.Printable;
import it.polimi.ingsw.client.view.cli.PrintableScene;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Player extends Printable {
    private final String nickname;
    private List<String> handLeaders;
    private List<String> activeLeaders;
    private List<String> productionBoard;
    private Map<Integer, String> warehouse;

    private PrintableScene productionSlotsScene, warehouseScene, activeLeadersScene, activeCardsScene, handScene, faithScene, cardScene;

    private final static String DEV_LEADER_SEPARATOR = "  ";
    private final static String WAREHOUSE_SLOTS_SEPARATOR = "       ";

    public Player(String nickname) {
        this.nickname = nickname;
        this.handLeaders = new ArrayList<String>(){{
            add("empty");
            add("empty");
        }};
        this.activeLeaders = new ArrayList<String>(){{
            add("empty");
            add("empty");
        }};
        this.productionBoard = new ArrayList<>();
        this.warehouse = new HashMap<>();
    }

    public String getNickname() {
        return nickname;
    }

    public void setHandLeaders(List<String> handLeaders) {
        if(handLeaders!=null) {
            this.handLeaders = handLeaders; //todo
        }
    }

    public void setActiveLeaders(List<String> activeLeaders) {
        if(activeLeaders!=null) {
            int i = 0;
            for(String id : activeLeaders) {
                this.activeLeaders.set(i, id);
                i++;
            }
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

    public List<String> getProductionBoard() {
        return productionBoard;
    }

    public void update(){
        Player player = Board.getBoard().getPlayerByNickname(nickname);
        player.setActiveLeaders(activeLeaders);
        player.setWarehouse(warehouse);
        player.setProductionBoard(productionBoard);
        player.setHandLeaders(handLeaders);
        player.setHandScene();
        player.setActiveLeadersScene();
        player.setProductionSlotsScene();
        player.setWarehouseScene();
    }

    public void setProductionSlotsScene() {
        List<Printable> devCards = productionBoard.stream().map(DevelopmentCard::new).collect(Collectors.toList());
        this.productionSlotsScene = new PrintableScene(PrintableScene.concatenatePrintable(devCards));
    }

    public void setWarehouseScene() {
        this.warehouseScene = new PrintableScene(new Inventory(warehouse));
    }

    public void setActiveLeadersScene() {
        List<Printable> leaderCards = activeLeaders.stream().map(LeaderCard::new).collect(Collectors.toList());
        this.activeLeadersScene = new PrintableScene(PrintableScene.concatenatePrintable(leaderCards));
    }

    public void setHandScene() {
        List<Printable> handCards = handLeaders.stream().map(LeaderCard::new).collect(Collectors.toList()); //todo
        this.handScene = new PrintableScene(PrintableScene.concatenatePrintable(handCards));;
    }

    public PrintableScene getWarehouseScene() {
        return warehouseScene;
    }

    @Override
    public List<String> getPrintable() {
        activeCardsScene = new PrintableScene(PrintableScene.concatenatePrintable(DEV_LEADER_SEPARATOR, productionSlotsScene, activeLeadersScene));
        cardScene = new PrintableScene(PrintableScene.addPrintablesToTop(handScene, " ", activeCardsScene));

        List<String> printable = PrintableScene.concatenatePrintable(WAREHOUSE_SLOTS_SEPARATOR, warehouseScene, cardScene).getPrintable();
        setWidth(printable);
        return printable;
    }

}

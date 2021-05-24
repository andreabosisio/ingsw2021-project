package it.polimi.ingsw.client.model;

import it.polimi.ingsw.client.view.cli.AnsiEnum;
import it.polimi.ingsw.client.view.cli.Printable;
import it.polimi.ingsw.client.view.cli.PrintableScene;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class PersonalBoard extends Printable {
    private final int ACTIVE_LEADERS_SLOTS = 2;
    private final int HAND_LEADERS_SLOTS = 2;

    private final String nickname;
    private final List<String> handLeaders;
    private final List<String> activeLeaders;
    private List<String> productionBoard;
    private Map<Integer, String> warehouse;

    private PrintableScene productionSlotsScene, warehouseScene, activeLeadersScene, activeCardsScene, handScene, faithScene, cardsScene;

    private final static String DEV_LEADER_SEPARATOR = "  ";
    private final static String WAREHOUSE_SLOTS_SEPARATOR = "       ";
    private final static int WAREHOUSE_BOARDNAME_OFFSET = 5;
    private final static int ACTIVE_HAND_LEADERS_OFFSET = 1;

    public PersonalBoard(String nickname) {
        this.nickname = nickname;
        this.handLeaders = Arrays.asList(LeaderCard.getEmptyCardID(), LeaderCard.getEmptyCardID());
        this.activeLeaders = Arrays.asList(LeaderCard.getEmptyCardID(), LeaderCard.getEmptyCardID());
        this.productionBoard = new ArrayList<>();
        this.warehouse = new HashMap<>();
    }

    public String getNickname() {
        return nickname;
    }

    public void setHandLeaders(List<String> handLeaders, String thisClientNickname) {
        if(handLeaders != null && this.nickname.equals(thisClientNickname)) {
            int i = 0;
            for (String id : handLeaders) {
                this.handLeaders.set(i, id);
                i++;
            }
            for(; i < this.handLeaders.size(); i++)
                this.handLeaders.set(i, LeaderCard.getEmptyCardID());
        }
    }

    public void setActiveLeaders(List<String> activeLeaders) {
        if(activeLeaders != null) {
            int i = 0;
            for(String id : activeLeaders) {
                this.activeLeaders.set(i, id);
                i++;
            }
        }
    }

    public void setProductionBoard(List<String> productionBoard) {
        if(productionBoard != null) {
            this.productionBoard = productionBoard;
        }
    }

    public void setWarehouse(Map<Integer, String> warehouse) {
        if(warehouse != null) {
            this.warehouse = warehouse;
        }
    }

    public List<String> getHandLeaders() {
        return handLeaders;
    }

    public List<String> getProductionBoard() {
        return productionBoard;
    }

    public void update(String thisClientNickname){
        PersonalBoard personalBoard = Board.getBoard().getPersonalBoardOf(nickname);
        personalBoard.setActiveLeaders(activeLeaders);
        personalBoard.setWarehouse(warehouse);
        personalBoard.setProductionBoard(productionBoard);
        personalBoard.setHandLeaders(handLeaders, thisClientNickname);
        personalBoard.setHandScene();
        personalBoard.setActiveLeadersScene();
        personalBoard.setProductionSlotsScene();
        personalBoard.setWarehouseScene();
        personalBoard.setActiveCardsScene();
        personalBoard.setCardsScene();
    }

    private void setProductionSlotsScene() {
        List<Printable> devCards = productionBoard.stream().map(DevelopmentCard::new).collect(Collectors.toList());
        AtomicInteger i = new AtomicInteger(-1);
        List<Printable> devCardsSlots = devCards.stream().map(c -> {
            i.getAndIncrement();
            return PrintableScene.addBottomString(c, "    [" + i +"]");
        }).collect(Collectors.toList());
        this.productionSlotsScene = new PrintableScene(PrintableScene.concatenatePrintable(devCardsSlots));
    }

    private void setWarehouseScene() {
        this.warehouseScene = new PrintableScene(PrintableScene.addBottomString(new Inventory(warehouse), getPrintableBoardName(), WAREHOUSE_BOARDNAME_OFFSET));
    }

    private String getPrintableBoardName() {
        return "       " + AnsiEnum.getPrettyNickname(getNickname()) + "'s Personal Board";
    }

    private void setActiveLeadersScene() {
        List<Printable> leaderCards = activeLeaders.stream().map(LeaderCard::new).collect(Collectors.toList());
        AtomicInteger i = new AtomicInteger(3);
        List<Printable> leaderSlots = leaderCards.stream().map(c -> {
            LeaderCard card = (LeaderCard) c;
            //if it's a production leader card
            if(card.getiD().charAt(0) == 'p') {
                i.getAndIncrement();
                return PrintableScene.addBottomString(c, "    [" + i +"]");
            }
            return PrintableScene.addBottomString(c, "    [X]");
        }).collect(Collectors.toList());
        this.activeLeadersScene = new PrintableScene(PrintableScene.concatenatePrintable(leaderSlots));
    }

    private void setHandScene() {
        List<Printable> handCards = handLeaders.stream().map(LeaderCard::new).collect(Collectors.toList());
        AtomicInteger i = new AtomicInteger(-1);
        List<Printable> handSlots = handCards.stream().map(c -> {
            i.getAndIncrement();
            return PrintableScene.addBottomString(c, "    [" + i +"]");
        }).collect(Collectors.toList());
        this.handScene = new PrintableScene(PrintableScene.concatenatePrintable(handSlots));
    }

    private void setCardsScene() {
        this.cardsScene = new PrintableScene(PrintableScene.addPrintablesToTop(handScene, ACTIVE_HAND_LEADERS_OFFSET, activeCardsScene));
    }

    private void setActiveCardsScene() {
        this.activeCardsScene = new PrintableScene(PrintableScene.concatenatePrintable(DEV_LEADER_SEPARATOR, productionSlotsScene, activeLeadersScene));
    }

    public PrintableScene getWarehouseScene() {
        return warehouseScene;
    }

    public PrintableScene getCardsScene() {
        return cardsScene;
    }

    public PrintableScene getActiveCardsScene() {
        return activeCardsScene;
    }

    public PrintableScene getProductionSlotsScene() {
        return productionSlotsScene;
    }

    @Override
    public List<String> getPrintable() {
        List<String> printable = PrintableScene.concatenatePrintable(WAREHOUSE_SLOTS_SEPARATOR, warehouseScene, cardsScene).getPrintable();
        setWidth(printable);
        return printable;
    }

}
package it.polimi.ingsw.client.model;

import it.polimi.ingsw.client.view.View;
import it.polimi.ingsw.client.view.cli.AnsiUtilities;
import it.polimi.ingsw.client.view.cli.Printable;
import it.polimi.ingsw.client.view.cli.PrintableScene;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class PersonalBoard extends Printable {

    private final String nickname;
    private final List<String> handLeaders;
    private final List<String> activeLeaders;
    private List<List<String>> productionBoard;

    private final List<LinkedHashSet<String>> developmentCardsInSlots = Arrays.asList(new LinkedHashSet<>(), new LinkedHashSet<>(), new LinkedHashSet<>(), new LinkedHashSet<>());
    private Map<Integer, String> warehouse;

    private PrintableScene productionSlotsScene, warehouseScene, activeLeadersScene, activeCardsScene, handScene, faithScene, cardsScene;

    private final static String DEV_LEADER_SEPARATOR = "  ";
    private final static String WAREHOUSE_SLOTS_SEPARATOR = "       ";
    private final static int WAREHOUSE_BOARDNAME_OFFSET = 5;
    private final static int ACTIVE_HAND_LEADERS_OFFSET = 1;

    public PersonalBoard(String nickname) {
        this.nickname = nickname;
        this.handLeaders = Arrays.asList(LeaderCard.EMPTY_CARD_ID, LeaderCard.EMPTY_CARD_ID);
        this.activeLeaders = Arrays.asList(LeaderCard.EMPTY_CARD_ID, LeaderCard.EMPTY_CARD_ID);
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
                this.handLeaders.set(i, LeaderCard.EMPTY_CARD_ID);
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

    public void setProductionBoard(List<List<String>> productionBoard) {
        if(productionBoard != null) {
            int i = 0;
            for(List<String> slot : productionBoard) {
                developmentCardsInSlots.get(i).addAll(slot);
                i++;
            }
            this.productionBoard = productionBoard;
        }
    }

    public void setWarehouse(Map<Integer, String> warehouse) {
        if(warehouse != null) {
            this.warehouse = warehouse;
        }
    }

    public Map<Integer, String> getWarehouse() {
        return warehouse;
    }

    public List<String> getHandLeaders() {
        return new ArrayList<>(handLeaders);
    }

    public List<String> getActiveLeaders() {
        return new ArrayList<>(activeLeaders);
    }

    public List<LinkedHashSet<String>> getDevelopmentCardsInSlots() {
        return developmentCardsInSlots;
    }

    public void update(View view){
        PersonalBoard personalBoard = Board.getBoard().getPersonalBoardOf(nickname);

        personalBoard.setActiveLeaders(activeLeaders);
        personalBoard.setWarehouse(warehouse);
        personalBoard.setProductionBoard(productionBoard);
        personalBoard.setHandLeaders(handLeaders, view.getNickname());
        
        view.personalBoardUpdate(personalBoard);

        //fixme why cli methods for gui?
        /*
        personalBoard.setHandScene();
        personalBoard.setActiveLeadersScene();
         */
        //personalBoard.setProductionSlotsScene();
        //personalBoard.setWarehouseScene();
        /*
        personalBoard.setActiveCardsScene();
        personalBoard.setCardsScene();
         */
    }

    public void updateCliScenes() {
        setHandScene();
        setActiveLeadersScene();
        setProductionSlotsScene();
        setWarehouseScene();
        setActiveCardsScene();
        setCardsScene();
    }

    private void setProductionSlotsScene() {
        List<Printable> slots = developmentCardsInSlots.stream().map(slot -> {
            Printable slotsBuilder = new PrintableScene(new ArrayList<>());
            if(slot.size() > 1)
                slot.removeIf(id -> id.equals(DevelopmentCard.EMPTY_CARD_ID));
            for (String id : slot) {
                slotsBuilder = DevelopmentCardsDatabase.getDevelopmentCardsDatabase().createDevelopmentCardByID(id).placeOnOtherCards(slotsBuilder);
            }
            return slotsBuilder;
        }).collect(Collectors.toList());
        AtomicInteger i = new AtomicInteger(-1);
        List<Printable> devCardsSlots = slots.stream().map(c -> {
            i.getAndIncrement();
            return PrintableScene.addBottomString(c, "    [" + i +"]");
        }).collect(Collectors.toList());
        this.productionSlotsScene = new PrintableScene(PrintableScene.concatenatePrintables(devCardsSlots));
    }

    private void setWarehouseScene() {
        this.warehouseScene = new PrintableScene(PrintableScene.addBottomString(new Inventory(warehouse, activeLeaders), getPrintableBoardName(), WAREHOUSE_BOARDNAME_OFFSET));
    }

    private String getPrintableBoardName() {
        return "       " + AnsiUtilities.getPrettyNickname(getNickname()) + "'s Personal Board";
    }

    private void setActiveLeadersScene() {
        List<Printable> leaderCards = activeLeaders.stream().map(LeaderCard::new).collect(Collectors.toList());
        AtomicInteger i = new AtomicInteger(3);
        List<Printable> leaderSlots = leaderCards.stream().map(c -> {
            LeaderCard card = (LeaderCard) c;
            //if it's a production leader card
            if(card.getID().charAt(0) == LeaderCard.PRODUCTION_LEADER_CARD_ID_PREFIX) {
                i.getAndIncrement();
                return PrintableScene.addBottomString(c, "    [" + i +"]");
            }
            return PrintableScene.addBottomString(c, "    [X]");
        }).collect(Collectors.toList());
        this.activeLeadersScene = new PrintableScene(PrintableScene.concatenatePrintables(leaderSlots));
    }

    private void setHandScene() {
        List<Printable> handCards = handLeaders.stream().map(LeaderCard::new).collect(Collectors.toList());
        AtomicInteger i = new AtomicInteger(-1);
        List<Printable> handSlots = handCards.stream().map(c -> {
            i.getAndIncrement();
            return PrintableScene.addBottomString(c, "    [" + i +"]");
        }).collect(Collectors.toList());
        this.handScene = new PrintableScene(PrintableScene.concatenatePrintables(handSlots));
    }

    private void setCardsScene() {
        this.cardsScene = new PrintableScene(PrintableScene.addPrintablesToTop(handScene, ACTIVE_HAND_LEADERS_OFFSET, activeCardsScene));
    }

    private void setActiveCardsScene() {
        this.activeCardsScene = new PrintableScene(PrintableScene.concatenatePrintables(DEV_LEADER_SEPARATOR, productionSlotsScene, activeLeadersScene));
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
        List<String> printable = PrintableScene.concatenatePrintables(WAREHOUSE_SLOTS_SEPARATOR, warehouseScene, cardsScene).getPrintable();
        setWidth(printable);
        return printable;
    }

}

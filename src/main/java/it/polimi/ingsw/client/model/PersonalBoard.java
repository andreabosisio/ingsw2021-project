package it.polimi.ingsw.client.model;

import it.polimi.ingsw.client.view.View;
import it.polimi.ingsw.client.view.cli.AnsiUtilities;
import it.polimi.ingsw.client.view.cli.Printable;
import it.polimi.ingsw.client.view.cli.PrintableScene;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Class that as the capacity to print the Personal Board of the Player
 */
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

    /**
     * Get method that return the nickname of the Player
     *
     * @return the nickname of the Player
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * This method set the Leader Card in hand of the Player
     *
     * @param handLeaders        is the list of the Leader Cards
     * @param thisClientNickname is the nickname of the Player to set
     */
    public void setHandLeaders(List<String> handLeaders, String thisClientNickname) {
        if (handLeaders != null && this.nickname.equals(thisClientNickname)) {
            int i = 0;
            for (String id : handLeaders) {
                this.handLeaders.set(i, id);
                i++;
            }
            for (; i < this.handLeaders.size(); i++)
                this.handLeaders.set(i, LeaderCard.EMPTY_CARD_ID);
        }
    }

    /**
     * This method set the active Leader Cards of the Player
     *
     * @param activeLeaders is the list of the active Leader Cards
     */
    public void setActiveLeaders(List<String> activeLeaders) {
        if (activeLeaders != null) {
            int i = 0;
            for (String id : activeLeaders) {
                this.activeLeaders.set(i, id);
                i++;
            }
        }
    }

    /**
     * This method set the Development Cards in the personal Board of the Player
     *
     * @param productionBoard is the list of the Development Cards activated
     */
    public void setProductionBoard(List<List<String>> productionBoard) {
        if (productionBoard != null) {
            int i = 0;
            for (List<String> slot : productionBoard) {
                developmentCardsInSlots.get(i).addAll(slot);
                i++;
            }
            this.productionBoard = productionBoard;
        }
    }

    /**
     * This method set the Warehouse of the Player
     *
     * @param warehouse is thw Warehouse of the Player
     */
    public void setWarehouse(Map<Integer, String> warehouse) {
        if (warehouse != null) {
            this.warehouse = warehouse;
        }
    }

    /**
     * Get method that return the Warehouse of the Player
     *
     * @return the Warehouse of the Player
     */
    public Map<Integer, String> getWarehouse() {
        return warehouse;
    }

    /**
     * Get method that return the Leader Cards in hand of the Player
     *
     * @return the Leader Cards in hand of the Player
     */
    public List<String> getHandLeaders() {
        return new ArrayList<>(handLeaders);
    }

    /**
     * Get method that return the activated Leader Card of the Player
     *
     * @return the activated Leader Card of the Player
     */
    public List<String> getActiveLeaders() {
        return new ArrayList<>(activeLeaders);
    }

    /**
     * Get method that return the Development Cards placed in the Personal Board
     *
     * @return the Development Cards placed in the Personal Board
     */
    public List<LinkedHashSet<String>> getDevelopmentCardsInSlots() {
        return developmentCardsInSlots;
    }

    /**
     * This method update all the data of the Personal Board of the Player
     *
     * @param view is the View of the Player
     */
    public void update(View view) {
        PersonalBoard personalBoard = Board.getBoard().getPersonalBoardOf(nickname);

        personalBoard.setActiveLeaders(activeLeaders);
        personalBoard.setWarehouse(warehouse);
        personalBoard.setProductionBoard(productionBoard);
        personalBoard.setHandLeaders(handLeaders, view.getNickname());

        view.personalBoardUpdate(personalBoard);

    }

    /**
     * This method is used to update the data of the Personal Board that
     * is show during the execution of the ClI application
     */
    public void updateCliScenes() {
        setHandScene();
        setActiveLeadersScene();
        setProductionSlotsScene();
        setWarehouseScene();
        setActiveCardsScene();
        setCardsScene();
    }

    /**
     * This method creates the scene that contains the slots with the Development Cards,
     * visible during the running of the CLI application
     */
    private void setProductionSlotsScene() {
        List<Printable> slots = developmentCardsInSlots.stream().map(slot -> {
            Printable slotsBuilder = new PrintableScene(new ArrayList<>());
            if (slot.size() > 1)
                slot.removeIf(id -> id.equals(DevelopmentCard.EMPTY_CARD_ID));
            for (String id : slot) {
                slotsBuilder = DevelopmentCardsDatabase.getDevelopmentCardsDatabase().createDevelopmentCardByID(id).placeOnOtherCards(slotsBuilder);
            }
            return slotsBuilder;
        }).collect(Collectors.toList());
        AtomicInteger i = new AtomicInteger(-1);
        List<Printable> devCardsSlots = slots.stream().map(c -> {
            i.getAndIncrement();
            return PrintableScene.addBottomString(c, "    [" + i + "]");
        }).collect(Collectors.toList());
        this.productionSlotsScene = new PrintableScene(PrintableScene.concatenatePrintables(devCardsSlots));
    }

    /**
     * This method creates the scene that contains the slots of the Warehouse,
     * visible during the running of the CLI application
     */
    private void setWarehouseScene() {
        this.warehouseScene = new PrintableScene(PrintableScene.addBottomString(new Inventory(warehouse, activeLeaders), getPrintableBoardName(), WAREHOUSE_BOARDNAME_OFFSET));
    }

    /**
     * Get method that return a better printable version of the name of the owner of the Personal Board
     *
     * @return a better printable version of the name of the owner of the Personal Board
     */
    private String getPrintableBoardName() {
        return "       " + AnsiUtilities.getPrettyNickname(getNickname()) + "'s Personal Board";
    }

    /**
     * This method creates the scene that contains the activated Leader Cards,
     * visible during the running of the CLI application
     */
    private void setActiveLeadersScene() {
        List<Printable> leaderCards = activeLeaders.stream().map(LeaderCard::new).collect(Collectors.toList());
        AtomicInteger i = new AtomicInteger(3);
        List<Printable> leaderSlots = leaderCards.stream().map(c -> {
            LeaderCard card = (LeaderCard) c;
            //if it's a production leader card
            if (card.getID().charAt(0) == LeaderCard.PRODUCTION_LEADER_CARD_ID_PREFIX) {
                i.getAndIncrement();
                return PrintableScene.addBottomString(c, "    [" + i + "]");
            }
            return PrintableScene.addBottomString(c, "    [X]");
        }).collect(Collectors.toList());
        this.activeLeadersScene = new PrintableScene(PrintableScene.concatenatePrintables(leaderSlots));
    }

    /**
     * This method creates the scene that contains the Leader Cards in hand,
     * visible during the running of the CLI application
     */
    private void setHandScene() {
        List<Printable> handCards = handLeaders.stream().map(LeaderCard::new).collect(Collectors.toList());
        AtomicInteger i = new AtomicInteger(-1);
        List<Printable> handSlots = handCards.stream().map(c -> {
            i.getAndIncrement();
            return PrintableScene.addBottomString(c, "    [" + i + "]");
        }).collect(Collectors.toList());
        this.handScene = new PrintableScene(PrintableScene.concatenatePrintables(handSlots));
    }

    /**
     * This method creates the scene that contains the Leader Cards in hand and activated,
     * visible during the running of the CLI application
     */
    private void setCardsScene() {
        this.cardsScene = new PrintableScene(PrintableScene.addPrintablesToTop(handScene, ACTIVE_HAND_LEADERS_OFFSET, activeCardsScene));
    }

    /**
     * This method creates the scene that contains the slots with the Development Cards and the Leader Cards activated
     * that has a production ability, visible during the running of the CLI application
     */
    private void setActiveCardsScene() {
        this.activeCardsScene = new PrintableScene(PrintableScene.concatenatePrintables(DEV_LEADER_SEPARATOR, productionSlotsScene, activeLeadersScene));
    }

    /**
     * Get method that return the Warehouse scene
     *
     * @return the Warehouse scene
     */
    public PrintableScene getWarehouseScene() {
        return warehouseScene;
    }

    /**
     * Get method that return the Cards scene that contains the Leader Cards in hand and activated
     *
     * @return the Cards scene
     */
    public PrintableScene getCardsScene() {
        return cardsScene;
    }

    /**
     * Get method that return the activatedCards scene that contains the slots with the Development Cards and the Leader Cards activated
     *
     * @return the activatedCards scene
     */
    public PrintableScene getActiveCardsScene() {
        return activeCardsScene;
    }

    /**
     * Get method that return the productionSlots scene that contains the slots with the Development Cards
     *
     * @return the productionSlotsScene
     */
    public PrintableScene getProductionSlotsScene() {
        return productionSlotsScene;
    }

    /**
     * Get method that return the printable version of all the Personal Board
     *
     * @return the printable version of all the Personal Board
     */
    @Override
    public List<String> getPrintable() {
        List<String> printable = PrintableScene.concatenatePrintables(WAREHOUSE_SLOTS_SEPARATOR, warehouseScene, cardsScene).getPrintable();
        setWidth(printable);
        return printable;
    }

}

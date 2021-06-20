package it.polimi.ingsw.client.view.gui.controllers;

import it.polimi.ingsw.client.events.send.*;
import it.polimi.ingsw.client.model.Board;
import it.polimi.ingsw.client.model.DevelopmentCardsDatabase;
import it.polimi.ingsw.client.view.gui.GUI;
import it.polimi.ingsw.client.view.gui.GUICommandListener;
import it.polimi.ingsw.client.view.gui.GraphicUtilities;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * This class is used as the controller for the fxml scene:boardScene.fxml,
 * it shows the Personal Board and the the common Game Board (Development Cards Grid, Market Tray)
 */
public class PersonalController extends GUICommandListener {
    private String nickname;
    private Stage leaderHandWindow;
    private HandController handController;
    private Stage transformationWindow;
    private Stage legendWindow;
    private LegendPopupController legendPopupController;
    private CardPlacementController cardPlacementController;
    private ProductionChoiceController productionChoiceController;
    private Stage cardPlacementWindow;
    private Stage productionChoiceWindow;
    private TransformationController transformationController;
    private final Map<Integer, List<Integer>> totalInResources = new HashMap<>();
    private final Map<Integer, String> totalOutResources = new HashMap<>();
    private final List<Node> currentSelectedResources = new ArrayList<>();
    private final List<Node> allSelectedResources = new ArrayList<>();
    private final List<Node> activatedProductions = new ArrayList<>();

    private boolean canSwap = false;
    private Node lastSwap;

    @FXML
    private AnchorPane legendPane;
    @FXML
    private ImageView board;
    @FXML
    private AnchorPane faithTrack;
    @FXML
    private AnchorPane popeTiles;
    @FXML
    private HBox HActiveProductionLeaders;
    @FXML
    private Button basicPower;
    @FXML
    private AnchorPane mainPane;
    @FXML
    private ImageView extraRes;
    @FXML
    private GridPane marketGrid;
    @FXML
    private GridPane devGrid;
    @FXML
    private Button handButton;
    @FXML
    private HBox HActiveLeaders;
    @FXML
    private VBox VArrowButtons;
    @FXML
    private HBox HArrowButtons;
    @FXML
    private HBox HResFromMarket;
    @FXML
    private VBox HLeadersRes;
    @FXML
    private GridPane strongboxGrid;
    @FXML
    private Button endProduction;
    @FXML
    private Button endTurn;
    @FXML
    private AnchorPane warehouse;
    @FXML
    private AnchorPane productionPane;
    @FXML
    private Button endSwap;

    /**
     * Function used to initialize the fxml when loaded.
     * It loads the current market state, the current Development Cards Grid and the current state of the Personal Board.
     * It set all the Buttons to allow the Player to start playing.
     */
    @FXML
    private void initialize() {
        //Load proper grids data
        GraphicUtilities.populateMarket(marketGrid, extraRes);
        GraphicUtilities.populateDevGrid(devGrid);
        GraphicUtilities.populateHandLeaders(HActiveLeaders, Board.getBoard().getPersonalBoardOf(nickname).getActiveLeaders());
        GraphicUtilities.populateProductionBoard(productionPane, nickname);
        GraphicUtilities.populateDepots(HResFromMarket, warehouse, HLeadersRes, strongboxGrid, nickname);
        GraphicUtilities.populateFaithTracks(faithTrack);
        GraphicUtilities.populatePopeTiles(popeTiles, nickname);
        GraphicUtilities.populateLegend(legendPane);
        //Prepare action for legend buttons
        for (Node n : legendPane.getChildren()) {
            Button button = (Button) n;
            if (button.getText().equals(nickname) || button.getText().equals(GraphicUtilities.getAiName())) {
                continue;
            }
            n.setOnMousePressed(event -> legendClick((Button) n));
        }
        //Prepare action for seeLeaderHand button
        handButton.setOnMousePressed(event -> showHandPopup());
        endSwap.setVisible(false);
        endSwap.setOnMousePressed(event -> sendSwapAction());
        endTurn.setOnMousePressed(event -> endTurnAction());
        //Prepare actions for market buttons
        for (Node b : Stream.concat(HArrowButtons.getChildren().stream(), VArrowButtons.getChildren().stream()).collect(Collectors.toList())) {
            Button button = (Button) b;
            button.setOnMousePressed((event -> marketAction(button.getId())));
        }

        //setup popup scene controllers
        handController = new HandController(nickname);
        handController.registerObservers(getCommandListenerObserver());
        transformationController = new TransformationController();
        transformationController.registerObservers(getCommandListenerObserver());
        cardPlacementController = new CardPlacementController(nickname);
        cardPlacementController.registerObservers(getCommandListenerObserver());
        productionChoiceController = new ProductionChoiceController(this);
        legendPopupController = new LegendPopupController();

        for (Node n : devGrid.getChildren()) {
            n.setOnMousePressed(event -> handleBuyRequest(n));
        }
        //set all resources buttons ID as their indexes
        int i = 0;
        //set from Market(0-3)
        for (Node n : HResFromMarket.getChildren()) {
            n.setId(String.valueOf(i));
            n.setOnMousePressed(event -> resourceClick(n));
            i++;
        }
        //set Warehouse (4-9)
        for (Node n : warehouse.getChildren()) {
            n.setId(String.valueOf(i));
            n.setOnMousePressed(event -> resourceClick(n));
            i++;
        }
        //set warehouseLeaders(10-13)
        for (Node n : HLeadersRes.getChildren()) {
            n.setId(String.valueOf(i));
            n.setOnMousePressed(event -> resourceClick(n));
            n.setVisible(false);
            i++;
        }
        //set strongBox(14...)
        for (Node n : strongboxGrid.getChildren()) {
            n.setId(String.valueOf(i));
            n.setOnMousePressed(event -> resourceClick(n));
            i++;
        }
        //set all normal productionBoard indexes
        i = 1;
        for (Node n : productionPane.getChildren()) {
            int buttonIndex = ((AnchorPane) n).getChildren().size() - 1;
            Button slot = (Button) ((AnchorPane) n).getChildren().get(buttonIndex);
            slot.setId(String.valueOf(i));
            slot.setOnMousePressed(event -> productionClick(slot));
            i++;
        }
        //prepare productionLeader special production
        i = 4;
        for (Node n : HActiveProductionLeaders.getChildren()) {
            n.setVisible(false);
            n.setOnMousePressed(event -> productionWithChoiceClick(n));
            n.setId(String.valueOf(i));
            i++;
        }
        //prepare basic special production
        basicPower.setOnMousePressed(event -> productionWithChoiceClick(basicPower));
        basicPower.setId(String.valueOf(0));
        //prepare end production button
        endProduction.setOnMousePressed(event -> endProductionClick());
        endProduction.setVisible(false);
        //repopulate leader warehouse and leader production special buttons
        List<String> activeLeaders = Board.getBoard().getPersonalBoardOf(nickname).getActiveLeaders();
        GraphicUtilities.populateActiveLeaders(activeLeaders, HActiveLeaders, HLeadersRes, HActiveProductionLeaders);

    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * This method is called when the Player click on the Your Hand Button:
     * it show a popup with the hand of the Player in it
     */
    private void showHandPopup() {
        FXMLLoader fxmlLoader = new FXMLLoader(GUI.class.getResource("/fxmls/leaderHandScene.fxml"));
        fxmlLoader.setController(handController);
        leaderHandWindow = GraphicUtilities.populatePopupWindow(mainPane.getScene().getWindow(), fxmlLoader, leaderHandWindow, Modality.WINDOW_MODAL);
        leaderHandWindow.show();
    }

    /**
     * This method is called when the Player has to choose the transformation of the White Resources:
     * it show a popup with the possibly colors of the resources to pick
     */
    public void showTransformationPopup(int numberOfTransformation, List<String> possibleTransformation) {
        Platform.runLater(() -> {
            FXMLLoader fxmlLoader = new FXMLLoader(GUI.class.getResource("/fxmls/whiteTransformation.fxml"));
            fxmlLoader.setController(transformationController);
            transformationController.setTransformation(numberOfTransformation, possibleTransformation);
            transformationWindow = GraphicUtilities.populatePopupWindow(mainPane.getScene().getWindow(), fxmlLoader, transformationWindow, Modality.WINDOW_MODAL);
            transformationWindow.show();
        });
    }

    /**
     * This method is called when the Player has to choose the slot to place the Development Card just bought:
     * it show a popup with the possibly slots to place the Card
     *
     * @param newCardID is the CardID of the Card to place
     */
    public void showCardPlacementPopup(String newCardID) {
        endTurn.setVisible(true);
        Platform.runLater(() -> {
            FXMLLoader fxmlLoader = new FXMLLoader(GUI.class.getResource("/fxmls/cardPlacement.fxml"));
            fxmlLoader.setController(cardPlacementController);
            cardPlacementController.setNewCardID(newCardID);
            cardPlacementWindow = GraphicUtilities.populatePopupWindow(mainPane.getScene().getWindow(), fxmlLoader, cardPlacementWindow, Modality.WINDOW_MODAL);
            cardPlacementWindow.show();
        });
    }

    /**
     * This method is called when the Player click on an arrow of the Market Tray:
     * it creates a new MarketActionEvent and it notify the commandListenerObserver with the Event just made
     *
     * @param arrowID is the ID of the Arrow
     */
    private void marketAction(String arrowID) {
        notifyObservers(new MarketActionEvent(Integer.parseInt(arrowID)));
    }

    /**
     * This method is called when the Player click on a Card of the Development Cards Grid:
     * it creates a new BuyActionEvent and it notify the commandListenerObserver with the Event just made
     *
     * @param n is the Button with the ID of the chosen Development Card
     */
    private void handleBuyRequest(Node n) {
        if (n.getId().equals(GraphicUtilities.getEmptyID())) {
            currentSelectedResources.clear();
            printErrorMessage("This slot is empty");
            return;
        }
        String color = DevelopmentCardsDatabase.getDevelopmentCardsDatabase().getColorOf(n.getId());
        int level = DevelopmentCardsDatabase.getDevelopmentCardsDatabase().getLevelOf(n.getId());
        List<Integer> resPositions = currentSelectedResources.stream().map(node -> Integer.parseInt(node.getId())).collect(Collectors.toList());
        notifyObservers(new BuyActionEvent(color, level, resPositions));
        currentSelectedResources.clear();
    }

    /**
     * This method is called when the Player click on a Resource:
     * it add the selected Resource in the List currentSelectedResources
     *
     * @param n
     */
    //todo: javaDOC
    private void resourceClick(Node n) {
        currentSelectedResources.add(n);
        if (canSwap) {
            if (lastSwap == null) {
                lastSwap = n;
            } else {
                Button last = (Button) lastSwap;
                Button selected = (Button) n;
                Node tmp = last.getGraphic();
                last.setGraphic(selected.getGraphic());
                selected.setGraphic(tmp);
                lastSwap = null;
            }
        }
    }

    /**
     * This method is used to update the state of the Market Tray:
     * all the resources are updated
     */
    public void marketUpdate() {
        if (mainPane == null)
            return;
        GraphicUtilities.populateMarket(marketGrid, extraRes);
    }

    /**
     * This method is used to update the state of the Development Cards Grid:
     * only the new Card is updated
     *
     * @param iD is the new Card to show
     */
    public void gridUpdate(String iD) {
        GraphicUtilities.updateDevGrid(devGrid, iD);
    }

    /**
     * This method is used to update the state of the Faith Tracks and of the Pope Tile
     */
    public void faithTracksAndPopeTilesUpdate() {
        if (mainPane == null)
            return;
        GraphicUtilities.populateFaithTracks(faithTrack);
        GraphicUtilities.populatePopeTiles(popeTiles, nickname);
    }

    /**
     * This method is used to update the activated Development Cards of the Player
     */
    public void productionBoardUpdate() {
        if (mainPane == null) {
            return;
        }
        GraphicUtilities.populateProductionBoard(productionPane, nickname);
    }

    /**
     * This method is used to update the state of the activated Leader Cards of the Player
     */
    public void activeLeadersUpdate() {
        if (mainPane == null) {
            return;
        }
        List<String> activeLeaders = Board.getBoard().getPersonalBoardOf(nickname).getActiveLeaders();
        GraphicUtilities.populateActiveLeaders(activeLeaders, HActiveLeaders, HLeadersRes, HActiveProductionLeaders);
    }

    /**
     * This method is used to update the state of the Inventory
     * (Warehouse, Strongbox, Resources from Market, Leader slots)
     */
    public void warehouseUpdate() {
        if (mainPane == null)
            return;
        GraphicUtilities.populateDepots(HResFromMarket, warehouse, HLeadersRes, strongboxGrid, nickname);
    }

    /**
     * This method is called when the Player has to choose the slots to place the Resources just take from the Market,
     * it sets visible the End Swap Button
     */
    public void activateSwaps() {
        currentSelectedResources.clear();
        endSwap.setVisible(true);
        canSwap = true;
    }

    /**
     * This method is called when the Player click on the End Swap Button:
     * it creates a new ResourcesPlacementActionEvent and it notify the commandListenerObserver with the Event just made,
     * it sets Visible the End Turn Button
     */
    private void sendSwapAction() {
        List<Integer> swaps = new ArrayList<>();
        currentSelectedResources.forEach(node -> swaps.add(Integer.parseInt(node.getId())));
        notifyObservers(new ResourcesPlacementActionEvent(swaps, true));
        endSwap.setVisible(false);
        currentSelectedResources.clear();
        canSwap = false;
        endTurn.setVisible(true);
    }

    /**
     * This method is called when the Player click on the End Turn Button:
     * it creates a new EndTurnActionEvent and it notify the commandListenerObserver with the Event just made,
     * it resets all the variables
     */
    private void endTurnAction() {
        currentSelectedResources.clear();
        totalInResources.clear();
        totalOutResources.clear();
        allSelectedResources.clear();
        activatedProductions.clear();
        endTurn.setVisible(false);
        notifyObservers(new EndTurnActionEvent());
    }

    /**
     * This method is called when it is not the turn of the Player:
     * it set a new image of the Board without the Faith Tracks
     */
    public void disableBoard() {
        board.setVisible(false);
        faithTrack.setVisible(false);
        popeTiles.setVisible(false);
    }

    /**
     * This method is called when it is the turn of the Player:
     * it set the correct image of the Board
     */
    public void activateBoard() {
        board.setVisible(true);
        faithTrack.setVisible(true);
        popeTiles.setVisible(true);
    }

    /**
     * This method is called when the Player click on the End Production Button:
     * it creates a new ProductionActionEvent and it notify the commandListenerObserver with the Event just made,
     * it reset the chosen resources
     */
    private void endProductionClick() {
        notifyObservers(new ProductionActionEvent(totalInResources, totalOutResources));
        totalInResources.clear();
        totalOutResources.clear();
        for (Node n : allSelectedResources) {
            n.setDisable(false);
        }
        for (Node n : activatedProductions) {
            n.setDisable(false);
        }
        endProduction.setVisible(false);
        endTurn.setVisible(true);
    }

    /**
     * This method is called when the Player has to do a Production with a choice of the Resources:
     * it show a popup with the possibly resources to choose
     *
     * @param production is the clicked Button (Basic Power, Leader Card of type Production)
     */
    private void productionWithChoiceClick(Node production) {
        productionClick(production);
        productionChoiceController.setProduction(production);
        FXMLLoader fxmlLoader = new FXMLLoader(GUI.class.getResource("/fxmls/productionChoiceScene.fxml"));
        fxmlLoader.setController(productionChoiceController);
        productionChoiceWindow = GraphicUtilities.populatePopupWindow(mainPane.getScene().getWindow(), fxmlLoader, productionChoiceWindow, Modality.WINDOW_MODAL);
        productionChoiceWindow.show();
    }

    /**
     * This method is called when the Player did a Production with a choice of the Resources:
     * it put in the list totalOutResources the chosen resources
     *
     * @param chosenResource are the chosen resources
     * @param production     is the clicked Button (Basic Power, Leader Card of type Production)
     */
    public void setChosenResource(String chosenResource, Node production) {
        totalOutResources.put(Integer.parseInt(production.getId()), chosenResource);
    }

    /**
     * This method is called when the Player click on a Card placed on his Personal Board:
     * it saves the selected resources, it sets Visible the End Production Button
     *
     * @param production is the chosen Development Card
     */
    private void productionClick(Node production) {
        List<Integer> resPositions = currentSelectedResources.stream().map(node -> Integer.parseInt(node.getId())).collect(Collectors.toList());
        totalInResources.put(Integer.parseInt(production.getId()), resPositions);
        for (Node n : currentSelectedResources) {
            n.setDisable(true);
        }
        activatedProductions.add(production);
        production.setDisable(true);
        allSelectedResources.addAll(currentSelectedResources);
        currentSelectedResources.clear();
        endTurn.setVisible(false);
        endProduction.setVisible(true);
    }

    /**
     * This method is called when the Player click on a nickname of another Player:
     * it show a popup with the current Personal Board of the Player to see
     *
     * @param nickname is the Player to see
     */
    private void legendClick(Button nickname) {
        FXMLLoader fxmlLoader = new FXMLLoader(GUI.class.getResource("/fxmls/legendPopupScene.fxml"));
        fxmlLoader.setController(legendPopupController);
        legendPopupController.setPlayerToSee(nickname.getText());
        legendWindow = GraphicUtilities.populatePopupWindow(mainPane.getScene().getWindow(), fxmlLoader, legendWindow, Modality.WINDOW_MODAL);
        legendWindow.show();
    }
}

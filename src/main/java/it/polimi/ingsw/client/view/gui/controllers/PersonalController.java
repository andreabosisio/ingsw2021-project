package it.polimi.ingsw.client.view.gui.controllers;

import it.polimi.ingsw.client.events.send.*;
import it.polimi.ingsw.client.model.Board;
import it.polimi.ingsw.client.model.DevelopmentCard;
import it.polimi.ingsw.client.model.DevelopmentCardsDatabase;
import it.polimi.ingsw.client.view.gui.GUI;
import it.polimi.ingsw.client.view.gui.GUICommandListener;
import it.polimi.ingsw.client.view.gui.GraphicUtilities;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.ImageCursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
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
    private Stage leaderHandWindow, transformationWindow, legendWindow, cardPlacementWindow, productionChoiceWindow;
    private HandController handController;
    private LegendPopupController legendPopupController;
    private CardPlacementController cardPlacementController;
    private ProductionChoiceController productionChoiceController;
    private TransformationController transformationController;
    private final Map<Integer, List<Integer>> totalInResources = new HashMap<>();
    private final Map<Integer, String> totalOutResources = new HashMap<>();
    private final List<Node> currentSelectedResources = new ArrayList<>();
    private final List<Node> allSelectedResources = new ArrayList<>();
    private final List<Node> activatedProductions = new ArrayList<>();

    private boolean canSwap = false;
    private Node lastSwap;

    @FXML
    AnchorPane legendPane;
    @FXML
    ImageView board;
    @FXML
    AnchorPane faithTrack;
    @FXML
    AnchorPane popeTiles;
    @FXML
    HBox HActiveProductionLeaders;
    @FXML
    Button basicPower;
    @FXML
    AnchorPane mainPane;
    @FXML
    ImageView extraRes;
    @FXML
    GridPane marketGrid;
    @FXML
    GridPane devGrid;
    @FXML
    Button handButton;
    @FXML
    HBox HActiveLeaders;
    @FXML
    VBox VArrowButtons;
    @FXML
    HBox HArrowButtons;
    @FXML
    HBox HResFromMarket;
    @FXML
    VBox HLeadersRes;
    @FXML
    GridPane strongboxGrid;
    @FXML
    Button endProduction;
    @FXML
    Button endTurn;
    @FXML
    AnchorPane warehouse;
    @FXML
    AnchorPane productionPane;
    @FXML
    Button endSwap;

    /**
     * Function used to initialize the fxml when loaded.
     * It loads the current market state, the current Development Cards Grid, the current state of the Personal Board and the faithTracks of each player with their names
     * It also pairs every button with its corresponding action to allow the Player to start playing.
     */
    @FXML
    private void initialize() {
        prepareProductions();
        prepareDepots();
        populateAll();
        prepareLegend();
        prepareActions();
        preparePopupsControllers();
    }

    /**
     * This method is used to setup every popup controller and sets the commandListenerObserver as observer if needed
     */
    private void preparePopupsControllers() {
        //setup popup scene controllers
        handController = new HandController(nickname);
        handController.registerObservers(getCommandListenerObserver());
        transformationController = new TransformationController();
        transformationController.registerObservers(getCommandListenerObserver());
        cardPlacementController = new CardPlacementController(nickname);
        cardPlacementController.registerObservers(getCommandListenerObserver());
        productionChoiceController = new ProductionChoiceController(this);
        legendPopupController = new LegendPopupController();
    }

    /**
     * This method is used to pair every button with its respective action
     */
    private void prepareActions() {
        //Prepare actions for grid buttons
        for (Node n : devGrid.getChildren()) {
            n.setOnMousePressed(event -> handleBuyRequest(n));
        }
        //Prepare actions for market buttons
        for (Node b : Stream.concat(HArrowButtons.getChildren().stream(), VArrowButtons.getChildren().stream()).collect(Collectors.toList())) {
            Button button = (Button) b;
            button.setOnMousePressed((event -> marketAction(button.getId())));
        }
        endSwap.setOnMousePressed(event -> sendSwapAction());
        endSwap.setVisible(false);
        endTurn.setOnMousePressed(event -> endTurnAction());
        endTurn.setVisible(false);
        handButton.setOnMousePressed(event -> showHandPopup());
    }

    /**
     * This method is used to load the graphic elements in every element of the gameBoard
     */
    private void populateAll() {
        GraphicUtilities.populateMarket(marketGrid, extraRes);
        GraphicUtilities.populateDevGrid(devGrid);
        GraphicUtilities.populateHandLeaders(HActiveLeaders, Board.getBoard().getPersonalBoardOf(nickname).getActiveLeaders());
        GraphicUtilities.populateProductionBoard(productionPane, nickname);
        GraphicUtilities.populateDepots(HResFromMarket, warehouse, HLeadersRes, strongboxGrid, nickname);
        GraphicUtilities.populateFaithTracks(faithTrack);
        GraphicUtilities.populatePopeTiles(popeTiles, nickname);
        GraphicUtilities.populateLegend(legendPane);
        //repopulate leader warehouse and leader production special buttons
        List<String> activeLeaders = Board.getBoard().getPersonalBoardOf(nickname).getActiveLeaders();
        GraphicUtilities.populateActiveLeaders(activeLeaders, HActiveLeaders, HLeadersRes, HActiveProductionLeaders);
    }

    /**
     * This method is used to prepare every production button id and its action on mousePressed
     */
    private void prepareProductions() {
        //set all normal productionBoard indexes
        int i = 1;
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
    }

    /**
     * This method is used to load each player name and color in the legend
     */
    private void prepareLegend() {
        //Prepare action for legend buttons
        for (Node n : legendPane.getChildren()) {
            Button button = (Button) n;
            if (button.getText().equals(nickname) || button.getText().equals(GraphicUtilities.getAiName())) {
                continue;
            }
            n.setOnMousePressed(event -> legendClick((Button) n));
        }
    }

    /**
     * This method is used to prepare each resource button id and sets its mousePressed action
     */
    private void prepareDepots() {
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
    }

    /**
     * This method is used to set the nickname of the player owner of this GUI
     *
     * @param nickname player's nickname
     */
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
     * it show a popup with the possibly colors of the resources to pick.
     *
     * @param numberOfTransformation The number of White Resource Transformations to apply
     * @param possibleTransformation A List containing Resource's colors of a possible transformation
     * @param scene Main scene
     */
    public void showTransformationPopup(int numberOfTransformation, List<String> possibleTransformation, Scene scene) {
        Platform.runLater(() -> {
            FXMLLoader fxmlLoader = new FXMLLoader(GUI.class.getResource("/fxmls/whiteTransformation.fxml"));
            fxmlLoader.setController(transformationController);
            transformationController.setTransformation(numberOfTransformation, possibleTransformation);
            transformationWindow = GraphicUtilities.populatePopupWindow(scene.getWindow(), fxmlLoader, transformationWindow, Modality.WINDOW_MODAL);
            transformationWindow.show();
        });
    }

    /**
     * This method is called when the Player has to choose the slot to place the Development Card just bought:
     * it show a popup with the possibly slots to place the Card
     *
     * @param newCardID is the CardID of the Card to place
     * @param scene Main scene
     */
    public void showCardPlacementPopup(String newCardID, Scene scene) {
        Platform.runLater(() -> {
            FXMLLoader fxmlLoader = new FXMLLoader(GUI.class.getResource("/fxmls/cardPlacement.fxml"));
            fxmlLoader.setController(cardPlacementController);
            cardPlacementController.setNewCardID(newCardID);
            cardPlacementWindow = GraphicUtilities.populatePopupWindow(scene.getWindow(), fxmlLoader, cardPlacementWindow, Modality.WINDOW_MODAL);
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
        for (Node resource : currentSelectedResources)
            resource.setDisable(false);
        currentSelectedResources.clear();
        notifyObservers(new MarketActionEvent(Integer.parseInt(arrowID)));
    }

    /**
     * This method is called when the Player click on a Card of the Development Cards Grid:
     * it creates a new BuyActionEvent and it notify the commandListenerObserver with the Event just made
     * the resources for the buy action are taken from the list currentSelectedResources which is cleared after the event is sent
     *
     * @param n is the Button with the ID of the chosen Development Card
     */
    private void handleBuyRequest(Node n) {
        if (n.getId().equals(DevelopmentCard.EMPTY_CARD_ID)) {
            printErrorMessage("This slot is empty");
        } else {
            String color = DevelopmentCardsDatabase.getDevelopmentCardsDatabase().getColorOf(n.getId());
            int level = DevelopmentCardsDatabase.getDevelopmentCardsDatabase().getLevelOf(n.getId());
            List<Integer> resPositions = currentSelectedResources.stream().map(node -> Integer.parseInt(node.getId())).collect(Collectors.toList());
            notifyObservers(new BuyActionEvent(color, level, resPositions));
        }
        for (Node res : currentSelectedResources)
            res.setDisable(false);
        currentSelectedResources.clear();
    }

    /**
     * This method is called when the Player click on a Resource:
     * It add the selected Resource in the List currentSelectedResources
     * If the player is performing a swap action it also swaps the imageViews of two resources when pressed one after the other
     *
     * @param n Node pressed by the player representing the resource in the GUI
     */
    private void resourceClick(Node n) {
        if (canSwap) {
            if (lastSwap == null) {
                lastSwap = n;
                Image nodeImage = ((Button)n).getGraphic().snapshot(new SnapshotParameters(), null);
                mainPane.getScene().setCursor(new ImageCursor(nodeImage));
            } else {
                mainPane.getScene().setCursor(null);
                Button last = (Button) lastSwap;
                Button selected = (Button) n;
                Node tmp = last.getGraphic();
                last.setGraphic(selected.getGraphic());
                selected.setGraphic(tmp);
                lastSwap = null;
            }
        } else {
            n.setDisable(true);
        }
        currentSelectedResources.add(n);
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
     * This method is called when the Player must perform a swap action in order to place the Resources taken from the Market,
     * it sets the player in canSwap mode and the endSwap Button as visible
     */
    public void activateSwaps() {
        setNonSwapButtonsTo(true);
        currentSelectedResources.clear();
        endSwap.setVisible(true);
        canSwap = true;
    }

    /**
     * This method is called when the Player click on the End Swap Button:
     * It creates a new ResourcesPlacementActionEvent and it notify the commandListenerObserver with the Event just made,
     * The swaps indexes are taken from the list currentSelectedResources which is cleared after the event is sent
     * it also sets the player as not in a swap phase and sets as invisible the endSwap button and as visible the End Turn Button
     */
    private void sendSwapAction() {
        setNonSwapButtonsTo(false);
        List<Integer> swaps = new ArrayList<>();
        currentSelectedResources.forEach(node -> swaps.add(Integer.parseInt(node.getId())));
        notifyObservers(new ResourcesPlacementActionEvent(swaps, true));
        endSwap.setVisible(false);
        currentSelectedResources.clear();
        canSwap = false;
    }

    /**
     * Set all non swap related buttons disable property.
     *
     * @param disable true to disable them, false otherwise
     */
    private void setNonSwapButtonsTo(boolean disable){
        handButton.setDisable(disable);
        HActiveProductionLeaders.setDisable(disable);
        productionPane.setDisable(disable);
        basicPower.setDisable(disable);
        HArrowButtons.setDisable(disable);
        VArrowButtons.setDisable(disable);
        devGrid.setDisable(disable);
        strongboxGrid.setDisable(disable);
    }

    /**
     * This method is called when the Player click on the End Turn Button:
     * it creates a new EndTurnActionEvent and it notify the commandListenerObserver with the Event just made,
     * it resets all the variables
     */
    private void endTurnAction() {
        currentSelectedResources.forEach(button -> button.setDisable(false));
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
    }

    /**
     * This method is called when it is the turn of the Player:
     * it set the correct image of the Board
     */
    public void activateBoard() {
        board.setVisible(true);
    }

    /**
     * This method is called when the Player click on the End Production Button:
     * it creates a new ProductionActionEvent and it notify the commandListenerObserver with the Event just made,
     * it resets both the totalInResources and totalOutResources lists
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
        //endTurn.setVisible(true);
    }

    /**
     * This method is called when the Player has to do a Production with a choice of the Resources:
     * It calls the productionClick function in order to add the selected resources to the list totalInResources
     * It also shows a popup with the possible resources to choose
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
     * This method is called when the Player did a Production with a resource choice:
     * it puts in the list totalOutResources the chosen resources
     *
     * @param chosenResource are the chosen resources
     * @param production     is the clicked Button (Basic Power, Leader Card of type Production)
     */
    public void setChosenResource(String chosenResource, Node production) {
        totalOutResources.put(Integer.parseInt(production.getId()), chosenResource);
    }

    /**
     * This method is called when the Player click on a Card placed on his Personal Board for a production without choice:
     * it saves the selected resources in the list totalInResources and also sets Visible the End Production Button
     *
     * @param production is the chosen Development Card
     */
    private void productionClick(Node production) {
        List<Integer> resPositions = currentSelectedResources.stream().map(node -> Integer.parseInt(node.getId())).collect(Collectors.toList());
        totalInResources.put(Integer.parseInt(production.getId()), resPositions);
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

    /**
     * This method is used to activate the end turn button in the GUI
     */
    public void activateEndButton() {
        endTurn.setVisible(true);
    }
}

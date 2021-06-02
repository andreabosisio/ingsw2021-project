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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;


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
    private Map<Integer, List<Integer>> totalInResources = new HashMap<>();
    private Map<Integer, String> totalOutResources = new HashMap<>();
    private List<Node> currentSelectedResources = new ArrayList<>();
    private List<Node> allSelectedResources = new ArrayList<>();
    private List<Node> activatedProductions = new ArrayList<>();

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

    @FXML
    private void initialize() {
        //Load proper grids data
        GraphicUtilities.populateMarket(marketGrid, extraRes);
        GraphicUtilities.populateDevGrid(devGrid);
        GraphicUtilities.populateHandLeaders(HActiveLeaders, Board.getBoard().getPersonalBoardOf(nickname).getActiveLeaders());
        GraphicUtilities.populateProductionBoard(productionPane, nickname);
        GraphicUtilities.populateWarehouse(HResFromMarket, warehouse, HLeadersRes, strongboxGrid, nickname);
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
            int buttonIndex = ((AnchorPane)n).getChildren().size() - 1;
            Button slot = (Button) ((AnchorPane)n).getChildren().get(buttonIndex);
            slot.setId(String.valueOf(i));
            //fixme: se production card è attivata nello slot 5, nel server è nello slot 4
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

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    private void showHandPopup() {
        FXMLLoader fxmlLoader = new FXMLLoader(GUI.class.getResource("/fxmls/leaderHandScene.fxml"));
        fxmlLoader.setController(handController);
        leaderHandWindow = GraphicUtilities.populatePopupWindow(mainPane.getScene().getWindow(), fxmlLoader, leaderHandWindow, Modality.WINDOW_MODAL);
        leaderHandWindow.show();
    }

    public void showTransformationPopup(int numberOfTransformation, List<String> possibleTransformation) {
        Platform.runLater(() -> {
            FXMLLoader fxmlLoader = new FXMLLoader(GUI.class.getResource("/fxmls/whiteTransformation.fxml"));
            fxmlLoader.setController(transformationController);
            transformationController.setTransformation(numberOfTransformation, possibleTransformation);
            transformationWindow = GraphicUtilities.populatePopupWindow(mainPane.getScene().getWindow(), fxmlLoader, transformationWindow, Modality.WINDOW_MODAL);
            transformationWindow.show();
        });
    }

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

    private void marketAction(String arrowID) {
        notifyObservers(new MarketActionEvent(Integer.parseInt(arrowID)));
    }

    private void handleBuyRequest(Node n) {
        //todo: find better solution???
        if (n.getId().equals("empty")) {
            currentSelectedResources.clear();
            printErrorMessage("No Cards selected");
            return;
        }
        String color = DevelopmentCardsDatabase.getDevelopmentCardsDatabase().getColorOf(n.getId());
        int level = DevelopmentCardsDatabase.getDevelopmentCardsDatabase().getLevelOf(n.getId());
        List<Integer> resPositions = currentSelectedResources.stream().map(node -> Integer.parseInt(node.getId())).collect(Collectors.toList());
        notifyObservers(new BuyActionEvent(color, level, resPositions));
        currentSelectedResources.clear();
    }

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

    public void marketUpdate() {
        if (mainPane == null)
            return;
        GraphicUtilities.populateMarket(marketGrid, extraRes);
    }

    public void gridUpdate(String iD) {
        GraphicUtilities.updateDevGrid(devGrid, iD);
    }

    public void faithTracksAndPopeTilesUpdate() {
        if (mainPane == null)
            return;
        GraphicUtilities.populateFaithTracks(faithTrack);
        GraphicUtilities.populatePopeTiles(popeTiles, nickname);
    }

    public void productionBoardUpdate() {
        if (mainPane == null) {
            return;
        }
        GraphicUtilities.populateProductionBoard(productionPane, nickname);
    }

    public void activeLeadersUpdate() {
        if (mainPane == null) {
            return;
        }
        List<String> activeLeaders = Board.getBoard().getPersonalBoardOf(nickname).getActiveLeaders();
        GraphicUtilities.populateActiveLeaders(activeLeaders, HActiveLeaders, HLeadersRes, HActiveProductionLeaders);
    }

    public void warehouseUpdate() {
        if (mainPane == null)
            return;
        GraphicUtilities.populateWarehouse(HResFromMarket, warehouse, HLeadersRes, strongboxGrid, nickname);
    }

    public void activateSwaps() {
        currentSelectedResources.clear();
        endSwap.setVisible(true);
        canSwap = true;
    }

    private void sendSwapAction() {
        List<Integer> swaps = new ArrayList<>();
        currentSelectedResources.forEach(node -> swaps.add(Integer.parseInt(node.getId())));
        notifyObservers(new ResourcesPlacementActionEvent(swaps, true));
        endSwap.setVisible(false);
        currentSelectedResources.clear();
        canSwap = false;
        endTurn.setVisible(true);
    }

    private void endTurnAction() {
        //todo reset all variables

        currentSelectedResources.clear();
        totalInResources.clear();
        totalOutResources.clear();
        allSelectedResources.clear();
        activatedProductions.clear();

        endTurn.setVisible(false);

        notifyObservers(new EndTurnActionEvent());
    }

    public void disableBoard() {
        board.setVisible(false);
        faithTrack.setVisible(false);
        popeTiles.setVisible(false);
    }

    public void activateBoard() {
        board.setVisible(true);
        faithTrack.setVisible(true);
        popeTiles.setVisible(true);
    }

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

    private void productionWithChoiceClick(Node production) {
        productionClick(production);
        productionChoiceController.setProduction(production);
        FXMLLoader fxmlLoader = new FXMLLoader(GUI.class.getResource("/fxmls/productionChoiceScene.fxml"));
        fxmlLoader.setController(productionChoiceController);
        productionChoiceWindow = GraphicUtilities.populatePopupWindow(mainPane.getScene().getWindow(), fxmlLoader, productionChoiceWindow, Modality.WINDOW_MODAL);
        productionChoiceWindow.show();
    }

    public void setChosenResource(String chosenResource, Node production) {
        totalOutResources.put(Integer.parseInt(production.getId()), chosenResource);
    }

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

    private void legendClick(Button b) {
        FXMLLoader fxmlLoader = new FXMLLoader(GUI.class.getResource("/fxmls/legendPopupScene.fxml"));
        fxmlLoader.setController(legendPopupController);
        legendPopupController.setPlayerToSee(b.getText());
        legendWindow = GraphicUtilities.populatePopupWindow(mainPane.getScene().getWindow(), fxmlLoader, legendWindow, Modality.WINDOW_MODAL);
        legendWindow.show();
    }

}

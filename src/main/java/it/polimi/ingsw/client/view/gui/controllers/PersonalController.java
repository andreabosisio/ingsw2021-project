package it.polimi.ingsw.client.view.gui.controllers;

import it.polimi.ingsw.client.events.send.EndTurnActionEvent;
import it.polimi.ingsw.client.events.send.MarketActionEvent;
import it.polimi.ingsw.client.events.send.ResourcesPlacementActionEvent;
import it.polimi.ingsw.client.model.Board;
import it.polimi.ingsw.client.model.DevelopmentCardsDatabase;
import it.polimi.ingsw.client.view.gui.GUI;
import it.polimi.ingsw.client.view.gui.GUICommandListener;
import it.polimi.ingsw.client.view.gui.GraphicUtilities;
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
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class PersonalController extends GUICommandListener {
    private String nickname;
    private Stage leaderHandWindow;
    private HandController handController;
    private Stage transformationWindow;
    private TransformationController transformationController;

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    private List<Node> currentSelectedResources = new ArrayList<>();
    private boolean canSwap = false;
    private Node lastSwap;
    @FXML
    private AnchorPane faithTrack;
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
    private Button buyCard;
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
        GraphicUtilities.populateLeaders(HActiveLeaders, Board.getBoard().getPersonalBoardOf(nickname).getActiveLeaders());
        GraphicUtilities.populateProductionBoard(productionPane,nickname);
        GraphicUtilities.populateWarehouse(HResFromMarket,warehouse,HLeadersRes,strongboxGrid,nickname);
        //Prepare action for seeLeaderHand button
        handButton.setOnMousePressed(event -> showHandPopup());
        endSwap.setVisible(false);
        endSwap.setOnMousePressed(event -> sendSwapAction());
        endTurn.setOnMousePressed(event -> endTurnAction());
        //Prepare actions for market buttons
        for (Node b : Stream.concat(HArrowButtons.getChildren().stream(), VArrowButtons.getChildren().stream()).collect(Collectors.toList())) {
            Button button = (Button) b;
            button.setOnMousePressed((event -> marketAction(button.getText())));
        }

        //setup popup scene controllers
        handController = new HandController(nickname);
        handController.registerObservers(getCommandListenerObserver());
        transformationController = new TransformationController();
        transformationController.registerObservers(getCommandListenerObserver());

        for (Node n : devGrid.getChildren()) {
            n.setOnMousePressed(event -> handleBuyRequest(n));
        }
        //setAllResources buttons ID as their indexes
        int i = 0;
        for (Node n : HResFromMarket.getChildren()) {
            n.setId(String.valueOf(i));
            n.setOnMousePressed(event -> resourceClick(n));
            i++;
        }
        for (Node n : warehouse.getChildren()) {
            n.setId(String.valueOf(i));
            n.setOnMousePressed(event -> resourceClick(n));
            i++;
        }
        for (Node n : HLeadersRes.getChildren()) {
            n.setId(String.valueOf(i));
            n.setOnMousePressed(event -> resourceClick(n));
            i++;
        }
        for (Node n : strongboxGrid.getChildren()) {
            n.setId(String.valueOf(i));
            n.setOnMousePressed(event -> resourceClick(n));
            i++;
        }
        //set all productionBoard indexes
        i = 0;
        for (Node n : productionPane.getChildren()) {
            n.setId(String.valueOf(i));
            n.setOnMousePressed(event -> productionClick(n.getId()));
            i++;
        }
    }

    private void showHandPopup() {
        FXMLLoader fxmlLoader = new FXMLLoader(GUI.class.getResource("/fxmls/leaderHandScene.fxml"));
        fxmlLoader.setController(handController);
        leaderHandWindow = GraphicUtilities.populatePopupWindow(mainPane.getScene().getWindow(), fxmlLoader, leaderHandWindow, Modality.WINDOW_MODAL);
        leaderHandWindow.show();
    }

    public void showTransformationPopup(int numberOfTransformation, List<String> possibleTransformation) {
        FXMLLoader fxmlLoader = new FXMLLoader(GUI.class.getResource("/fxmls/whiteTransformation.fxml"));
        fxmlLoader.setController(transformationController);
        transformationController.setTransformation(numberOfTransformation, possibleTransformation);
        transformationWindow = GraphicUtilities.populatePopupWindow(mainPane.getScene().getWindow(), fxmlLoader, transformationWindow, Modality.WINDOW_MODAL);
        transformationWindow.show();
    }

    private void marketAction(String arrowID) {
        notifyObservers(new MarketActionEvent(Integer.parseInt(arrowID)));
    }

    private void handleBuyRequest(Node n) {
        String color = DevelopmentCardsDatabase.getDevelopmentCardsDatabase().getColorOf(n.getId());
        int level = DevelopmentCardsDatabase.getDevelopmentCardsDatabase().getLevelOf(n.getId());
        List<Integer> resPositions = currentSelectedResources.stream().map(node -> Integer.parseInt(node.getId())).collect(Collectors.toList());
        System.out.println("buy of " + color + " " + level + " " + resPositions);
        //notifyObservers(new BuyActionEvent(color,level,resPositions));
        currentSelectedResources.clear();
    }

    private void resourceClick(Node n) {
        currentSelectedResources.add(n);
        if(canSwap){
            if(lastSwap==null){
                lastSwap = n;
            }
            else{
                Button last = (Button)lastSwap;
                Button selected = (Button) n;
                Node tmp = last.getGraphic();
                last.setGraphic(selected.getGraphic());
                selected.setGraphic(tmp);
                lastSwap = null;
            }
        }

    }

    private void productionClick(String prodID) {
        //List<Integer> resPositions = currentSelectedResources.stream().map(node->Integer.parseInt(node.getId())).collect(Collectors.toList());
        System.out.println(prodID);
    }

    public void marketUpdate() {
        if(mainPane==null)
            return;
        GraphicUtilities.populateMarket(marketGrid, extraRes);
    }

    public void gridUpdate(String iD) {
        GraphicUtilities.updateDevGrid(devGrid, iD);
    }

    public void faithTracksUpdate() {
        //GraphicUtilities.populateFaithTracks();
    }

    public void productionBoardUpdate() {
    }

    public void activeLeadersUpdate() {
    }

    public void warehouseUpdate() {
        if(mainPane==null)
            return;
        GraphicUtilities.populateWarehouse(HResFromMarket,warehouse,HLeadersRes,strongboxGrid,nickname);
    }
    public void activateSwaps(){
        endSwap.setVisible(true);
        canSwap = true;
    }
    private void sendSwapAction(){
        List<Integer> swaps = new ArrayList<>();
        currentSelectedResources.forEach(node->swaps.add(Integer.parseInt(node.getId())));
        notifyObservers(new ResourcesPlacementActionEvent(swaps,true));
        endSwap.setVisible(false);
        currentSelectedResources.clear();
        canSwap=false;
    }
    private void endTurnAction(){
        //todo reset all variables
        notifyObservers(new EndTurnActionEvent());

    }
}

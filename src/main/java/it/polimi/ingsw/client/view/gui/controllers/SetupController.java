package it.polimi.ingsw.client.view.gui.controllers;

import it.polimi.ingsw.client.events.send.ChosenSetupEvent;
import it.polimi.ingsw.client.view.gui.GUI;
import it.polimi.ingsw.client.view.gui.GUICommandListener;
import it.polimi.ingsw.client.view.gui.GraphicUtilities;
import it.polimi.ingsw.commons.enums.StorableResourceEnum;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used as the controller for the fxml scene:setupScene.fxml
 */
public class SetupController extends GUICommandListener {
    private int numberOfResources;
    private Stage marketWindow;
    private Stage gridWindow;
    private static final String PATH_TO_MARKET_POPUP_FXML = "/fxmls/marketPopup.fxml";
    private static final String PATH_TO_GRID_POPUP_FXML = "/fxmls/devGridPopupScene.fxml";

    private List<String> leaderCardsID;
    private List<Integer> chosenLeadersIndexes;
    private List<String> chosenResources;
    private static final String waitMessage = "Setup done. Please wait for other players' choices.";

    @FXML
    private Button done;

    @FXML
    private AnchorPane mainPane;

    @FXML
    private Button gridButton;

    @FXML
    private Button marketButton;

    @FXML
    private HBox HToggleLeaders;

    @FXML
    private HBox HToggleResources;

    /**
     * Function used to initialize the fxml when loaded
     * It loads the leader cards the player can choose from in the corresponding buttons
     * It also activates the buttons to choose the resources if the player has the right to
     * It then pairs each button with its corresponding action
     */
    @FXML
    public void initialize() {
        File file;
        int i = 0;
        for (String leaderID : leaderCardsID) {
            ToggleButton toggleButton = (ToggleButton) HToggleLeaders.getChildren().get(i);
            toggleButton.setId(String.valueOf(i));
            GraphicUtilities.loadLeaderImage(toggleButton, leaderID);
            i++;
        }
        for (i = 0; i < numberOfResources; i++) {
            Button button = (Button) HToggleResources.getChildren().get(i);
            button.setId("0");
            GraphicUtilities.loadResource(button);
            button.setDisable(false);
            button.setOnMousePressed((event -> changeResourceAction(button)));
        }
        done.setOnMousePressed((event -> setupAction()));
        gridButton.setOnMousePressed((event -> seeGrid()));
        marketButton.setOnMousePressed((event -> seeMarket()));
    }

    /**
     * This method is used to set from which leaderCard IDs the player can choose and how many resources he deserves
     *
     * @param leaderCardsID    list with the IDs of each leader card
     * @param numberOfResource number of resources to choose
     */
    public void initializeData(List<String> leaderCardsID, int numberOfResource) {
        this.leaderCardsID = leaderCardsID;
        this.numberOfResources = numberOfResource;
    }

    /**
     * This method is called when the player wishes to send to the server his choices
     * If he has chosen less than 2 cards an error message is printed
     * If the data is valid the method construct an event to send to server with all the necessary data
     * Every popup associated with this window is then closed
     */
    private void setupAction() {
        chosenLeadersIndexes = new ArrayList<>();
        for (Node node : HToggleLeaders.getChildren()) {
            ToggleButton button = (ToggleButton) node;
            if (button.isSelected()) {
                try {
                    chosenLeadersIndexes.add(Integer.parseInt(button.getId()));
                } catch (NumberFormatException e) {
                    printErrorMessage("Failed to extract index of chosen cards");
                }
            }
        }
        if (chosenLeadersIndexes.size() != 2) {
            printErrorMessage("You must choose 2 Leaders");
            return;
        }
        chosenResources = new ArrayList<>();
        for (int i = 0; i < numberOfResources; i++) {
            Button button = (Button) HToggleResources.getChildren().get(i);
            if (!button.isDisable()) {
                chosenResources.add(StorableResourceEnum.values()[Integer.parseInt(button.getId())].toString());
            }
        }
        if (marketWindow != null) {
            marketWindow.close();
        }
        if (gridWindow != null) {
            gridWindow.close();
        }
        notifyObservers(new ChosenSetupEvent(chosenLeadersIndexes, chosenResources));
        printInfoMessage(waitMessage);
        done.setDisable(true);
    }

    /**
     * This method is used to change the resource associated with the pressed button
     *
     * @param button button pressed
     */
    private void changeResourceAction(Button button) {
        GraphicUtilities.loopResources(button);
    }

    /**
     * This method is used to show a popup containing the market saved in the reduced model
     */
    private void seeMarket() {
        FXMLLoader fxmlLoader = new FXMLLoader(GUI.class.getResource(PATH_TO_MARKET_POPUP_FXML));
        marketWindow = GraphicUtilities.populatePopupWindow(mainPane.getScene().getWindow(), fxmlLoader, marketWindow, Modality.NONE);
        marketWindow.show();
    }

    /**
     * This method is used to show a popup containing the development cards grid saved in the reduced model
     */
    private void seeGrid() {
        FXMLLoader fxmlLoader = new FXMLLoader(GUI.class.getResource(PATH_TO_GRID_POPUP_FXML));
        gridWindow = GraphicUtilities.populatePopupWindow(mainPane.getScene().getWindow(), fxmlLoader, gridWindow, Modality.NONE);
        gridWindow.show();
    }
}



package it.polimi.ingsw.client.view.gui.controllers;

import it.polimi.ingsw.client.model.Board;
import it.polimi.ingsw.client.view.gui.GraphicUtilities;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * This class is used as the controller for the fxml scene:legendPopupScene.fxml
 * It shows the unresponsive board of one player in the game
 */
public class LegendPopupController {
    private String playerToSee;
    @FXML
    AnchorPane mainPane;
    @FXML
    Button done;
    @FXML
    HBox ignore;
    @FXML
    GridPane strongboxGrid;
    @FXML
    AnchorPane warehouse;
    @FXML
    AnchorPane productionPane;
    @FXML
    VBox HLeadersRes;
    @FXML
    HBox HActiveLeaders;
    @FXML
    private void initialize() {
        done.setOnMousePressed(event -> doneAction());
        GraphicUtilities.populateHandLeaders(HActiveLeaders, Board.getBoard().getPersonalBoardOf(playerToSee).getActiveLeaders());
        GraphicUtilities.populateProductionBoard(productionPane,playerToSee);
        ignore.setVisible(false);
        GraphicUtilities.populateDepots(ignore,warehouse,HLeadersRes,strongboxGrid,playerToSee);
    }

    /**
     * This method is used to set which player' board to show
     *
     * @param playerToSee nickname of the player to spy on
     */
    public void setPlayerToSee(String playerToSee){
        this.playerToSee = playerToSee;
    }

    /**
     * This method is called when the player presses the done button in order to close this popup
     */
    private void doneAction(){
        Stage stage = (Stage) mainPane.getScene().getWindow();
        stage.close();
    }
}

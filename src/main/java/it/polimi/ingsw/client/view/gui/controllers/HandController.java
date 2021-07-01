package it.polimi.ingsw.client.view.gui.controllers;

import it.polimi.ingsw.client.events.send.LeaderActionEvent;
import it.polimi.ingsw.client.model.Board;
import it.polimi.ingsw.client.view.gui.GUICommandListener;
import it.polimi.ingsw.client.view.gui.GraphicUtilities;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * This class is used as the controller for the fxml scene:leaderHandScene.fxml
 */
public class HandController extends GUICommandListener {
    private final String nickname;

    public HandController(String nickname) {
        this.nickname = nickname;
    }

    @FXML
    HBox HLeaders;
    @FXML
    AnchorPane mainPane;
    @FXML
    Button discard1;
    @FXML
    Button discard2;
    @FXML
    Button activate1;
    @FXML
    Button activate2;

    /**
     * Function used to initialize the fxml when loaded
     * It does so by loading the correct imageView for each leaderCard in the player' hand
     * It also sets discardLeader() on a mousePressed of the 2 discard buttons
     * and activateLeader() on a mousePressed of the 2 activate buttons
     */
    @FXML
    private void initialize() {
        GraphicUtilities.populateHandLeaders(HLeaders, Board.getBoard().getPersonalBoardOf(nickname).getHandLeaders());
        discard1.setOnMousePressed(event -> discardLeader(discard1.getId()));
        discard2.setOnMousePressed(event -> discardLeader(discard2.getId()));
        activate1.setOnMousePressed(event -> activateLeader(activate1.getId()));
        activate2.setOnMousePressed(event -> activateLeader(activate2.getId()));
    }

    /**
     * This method is called when a player wishes to discard one of his leaders
     * It construct an event to send to the server with the index of the chosen card
     *
     * @param index index of the leader card to discard
     */
    private void discardLeader(String index) {
        String leaderID = Board.getBoard().getPersonalBoardOf(nickname).getHandLeaders().get(Integer.parseInt(index));
        notifyObservers(new LeaderActionEvent(leaderID, true));
        Stage stage = (Stage) mainPane.getScene().getWindow();
        stage.close();
    }

    /**
     * This method is called when a player wishes to activate one of his leaders
     * It construct an event to send to the server with the index of the chosen card
     *
     * @param index index of the leader card to activate
     */
    private void activateLeader(String index) {
        String leaderID = Board.getBoard().getPersonalBoardOf(nickname).getHandLeaders().get(Integer.parseInt(index));
        notifyObservers(new LeaderActionEvent(leaderID, false));
        Stage stage = (Stage) mainPane.getScene().getWindow();
        stage.close();
    }
}

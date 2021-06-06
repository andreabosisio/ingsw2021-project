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

    @FXML
    private void initialize() {
        GraphicUtilities.populateHandLeaders(HLeaders, Board.getBoard().getPersonalBoardOf(nickname).getHandLeaders());
        discard1.setOnMousePressed(event -> discardLeader(discard1.getId()));
        discard2.setOnMousePressed(event -> discardLeader(discard2.getId()));
        activate1.setOnMousePressed(event -> activateLeader(activate1.getId()));
        activate2.setOnMousePressed(event -> activateLeader(activate2.getId()));
    }

    private void discardLeader(String index) {
        String leaderID = Board.getBoard().getPersonalBoardOf(nickname).getHandLeaders().get(Integer.parseInt(index));
        notifyObservers(new LeaderActionEvent(leaderID, true));
        Stage stage = (Stage) mainPane.getScene().getWindow();
        stage.close();
    }

    private void activateLeader(String index) {
        String leaderID = Board.getBoard().getPersonalBoardOf(nickname).getHandLeaders().get(Integer.parseInt(index));
        notifyObservers(new LeaderActionEvent(leaderID, false));
        Stage stage = (Stage) mainPane.getScene().getWindow();
        stage.close();
    }
}

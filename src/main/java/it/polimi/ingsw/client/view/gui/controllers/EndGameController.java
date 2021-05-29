package it.polimi.ingsw.client.view.gui.controllers;

import it.polimi.ingsw.client.view.gui.GUICommandListener;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.util.Map;

public class EndGameController extends GUICommandListener {
    @FXML
    private TextField winnerBox;
    @FXML
    private TextArea victoryPointsBox;

    public void showEndGameEvent(String winner, Map<String, Integer> playersPoints) {
        StringBuilder victoryPoints = new StringBuilder("The Victory Points of all the players are: \n");

        winnerBox.setText("The winner is: " + winner);

        for (Map.Entry<String, Integer> entry : playersPoints.entrySet()) {
            victoryPoints.append(entry.getKey())
                    .append(" : ")
                    .append(entry.getValue())
                    .append("\n");
        }

        victoryPointsBox.setText(victoryPoints.toString());
    }
}
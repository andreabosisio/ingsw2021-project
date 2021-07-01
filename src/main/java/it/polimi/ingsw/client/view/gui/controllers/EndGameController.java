package it.polimi.ingsw.client.view.gui.controllers;

import it.polimi.ingsw.client.view.gui.GUICommandListener;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.util.Map;

/**
 * This class is used as the controller for the fxml scene:endGameScene.fxml
 * It doesn't allow any action from the player
 */
public class EndGameController extends GUICommandListener {
    @FXML
    private TextField winnerBox;
    @FXML
    private TextArea victoryPointsBox;

    /**
     * This method is used to show the winner and each player points
     *
     * @param winner        nickname of the winning player
     * @param playersPoints map <nickname,points> of every player in the game
     */
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
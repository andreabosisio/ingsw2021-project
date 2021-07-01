package it.polimi.ingsw.client.view.gui.controllers;

import it.polimi.ingsw.client.events.send.SelectNumberPlayersEvent;
import it.polimi.ingsw.client.view.gui.GUICommandListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;

/**
 * This class is used as the controller for the fxml scene:chooseNumberScene.fxml
 */
public class ChooseNumberController extends GUICommandListener {
    ObservableList<Integer> playerNumbers = FXCollections.observableArrayList(1, 2, 3, 4);
    @FXML
    private Button done;
    @FXML
    private ComboBox<Integer> numberSelector;

    /**
     * Function used to initialize the fxml when loaded
     * It does so by loading the possible number of players from which the player can choose and
     * it sets the doneAction() when the done button is pressed
     */
    @FXML
    private void initialize() {
        numberSelector.setItems(playerNumbers);
        numberSelector.setValue(playerNumbers.get(0));
        done.setOnMousePressed((event -> doneAction()));
    }

    /**
     * This method is called when the player chooses the number of players he wish to play against
     * It construct the event to send to the server with the number chosen
     */
    private void doneAction() {
        notifyObservers(new SelectNumberPlayersEvent(numberSelector.getValue()));
    }
}

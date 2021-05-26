package it.polimi.ingsw.client.view.gui.controllers;

import it.polimi.ingsw.client.events.send.SelectNumberPlayersEvent;
import it.polimi.ingsw.client.view.gui.GUICommandListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;

public class ChooseNumberController extends GUICommandListener {
    ObservableList<Integer> playerNumbers = FXCollections.observableArrayList(1,2,3,4);
    @FXML
    private Button done;
    @FXML
    private ComboBox<Integer> numberSelector;
    @FXML
    private void initialize(){
        numberSelector.setItems(playerNumbers);
        numberSelector.setValue(playerNumbers.get(0));
        done.setOnMousePressed((event -> doneAction()));
    }
    private void doneAction(){
        notifyObservers(new SelectNumberPlayersEvent(numberSelector.getValue()));
    }
}

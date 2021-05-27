package it.polimi.ingsw.client.view.gui.controllers;

import it.polimi.ingsw.client.view.gui.GUICommandListener;
import it.polimi.ingsw.client.view.gui.GraphicUtilities;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;


public class PersonalController extends GUICommandListener {
    @FXML
    private ImageView extraRes;
    @FXML
    private GridPane marketGrid;
    @FXML
    GridPane devGrid;
    @FXML
    Button handButton;
    @FXML
    HBox HActiveLeaders;
    @FXML
    private void initialize() {
        GraphicUtilities.populateMarket(marketGrid,extraRes);
        GraphicUtilities.populateDevGrid(devGrid);
        handButton.setOnMousePressed(event -> showHandAction());
    }
    private void showHandAction(){
        System.out.println("you pressend for hand :)");
    }
}

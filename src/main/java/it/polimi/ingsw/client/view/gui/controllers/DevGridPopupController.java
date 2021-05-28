package it.polimi.ingsw.client.view.gui.controllers;

import it.polimi.ingsw.client.view.gui.GraphicUtilities;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;

public class DevGridPopupController {
    @FXML
    GridPane devGrid;
    @FXML
    private void initialize() {
        //todo disable or do nothing?
        /*
        for(Node n:devGrid.getChildren()){
            n.setDisable(true);
        }

         */
        GraphicUtilities.populateDevGrid(devGrid);
    }


}

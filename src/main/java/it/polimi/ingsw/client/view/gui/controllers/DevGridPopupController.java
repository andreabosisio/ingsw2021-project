package it.polimi.ingsw.client.view.gui.controllers;

import it.polimi.ingsw.client.view.gui.GraphicUtilities;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;

/**
 * This class is used as the controller of the devGrid popup
 * It doesn't allow for any action on it
 */
public class DevGridPopupController {
    @FXML
    GridPane devGrid;

    /**
     * Function used to initialize the fxml when loaded
     * It loads the current devGrid state in the scene' gridPane
     */
    @FXML
    private void initialize() {
        GraphicUtilities.populateDevGrid(devGrid);
    }
}

package it.polimi.ingsw.client.view.gui.controllers;

import it.polimi.ingsw.client.view.gui.GUICommandListener;
import it.polimi.ingsw.client.view.gui.GraphicUtilities;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

/**
 * This class is used as the controller of the market popup
 * It doesn't allow for any action on it
 */
public class MarketController extends GUICommandListener {

    @FXML
    private ImageView extraRes;
    @FXML
    private GridPane marketGrid;

    /**
     * Function used to initialize the fxml when loaded
     * It loads the current market state in the scene' gridPane
     * and the extra resource in the imageView
     */
    @FXML
    private void initialize() {
        GraphicUtilities.populateMarket(marketGrid, extraRes);
    }
}

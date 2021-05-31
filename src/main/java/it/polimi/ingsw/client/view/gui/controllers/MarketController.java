package it.polimi.ingsw.client.view.gui.controllers;

import it.polimi.ingsw.client.view.gui.GUICommandListener;
import it.polimi.ingsw.client.view.gui.GraphicUtilities;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.util.List;

public class MarketController extends GUICommandListener {

    private List<String> fullMarket;
    @FXML
    private ImageView extraRes;
    @FXML
    private GridPane marketGrid;

    @FXML
    private void initialize() {
        //populate the marketGrid with savedData
        GraphicUtilities.populateMarket(marketGrid, extraRes);
    }
}

package it.polimi.ingsw.client.view.gui.controllers;

import it.polimi.ingsw.client.events.send.MarketActionEvent;
import it.polimi.ingsw.client.model.Board;
import it.polimi.ingsw.client.view.gui.GUICommandListener;
import it.polimi.ingsw.client.view.gui.GraphicUtilities;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MarketController extends GUICommandListener {

    private boolean viewOnly;
    private List<String> fullMarket;
    @FXML
    private ImageView extraRes;
    @FXML
    private GridPane marketGrid;
    @FXML
    private VBox VButtons;
    @FXML
    private HBox HButtons;

    //todo remove this constructor because controller can be set in fxml
    public MarketController(boolean viewOnly) {
        this.viewOnly = viewOnly;
    }

    @FXML
    private void initialize() {
        //populate the marketGrid with savedData
        GraphicUtilities.populateMarket(marketGrid, extraRes);
        VButtons.setDisable(true);
        HButtons.setDisable(true);
    }
}

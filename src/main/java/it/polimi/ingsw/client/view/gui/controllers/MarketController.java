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

    public MarketController(boolean viewOnly) {
        this.viewOnly = viewOnly;
    }

    @FXML
    private void initialize() {
        //populate the marketGrid with savedData
        GraphicUtilities.populateMarket(marketGrid, extraRes);
        if (viewOnly) {
            HButtons.setDisable(viewOnly);
            VButtons.setDisable(viewOnly);
            return;
        }
        printInfoMessage("Click an arrow to take the resources");
        for (Node b : Stream.concat(HButtons.getChildren().stream(), VButtons.getChildren().stream()).collect(Collectors.toList())) {
            Button button = (Button) b;
            button.setOnMousePressed((event -> marketAction(button.getText())));
        }
    }

    private void setButtons(boolean disable) {
        VButtons.setDisable(disable);
        HButtons.setDisable(disable);
    }

    public void setOnViewOnly() {
        setButtons(true);
    }

    public void marketAction(String arrowID) {
        System.out.println(arrowID);
        //notifyObservers(new MarketActionEvent(Integer.parseInt(arrowID)));
    }

    public void update(List<String> marketUpdated) {
        fullMarket = marketUpdated;
    }

}

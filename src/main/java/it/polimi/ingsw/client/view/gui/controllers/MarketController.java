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

public class MarketController extends GUICommandListener {

    private List<String> fullMarket;
    @FXML
    private ImageView extraRes;
    @FXML
    private Button arrow_0;
    @FXML
    private GridPane marketGrid;
    @FXML
    private VBox VButtons;
    @FXML
    private HBox HButtons;
    @FXML
    private void initialize(){
        printInfoMessage("Click an arrow to take the resources");
        //populate the marketGrid with savedData
        GraphicUtilities.populateMarket(marketGrid,extraRes);

        //todo merge 2 for below
        for(Node b:HButtons.getChildren()){
            Button button = (Button)b;
            button.setOnMousePressed((event -> marketAction(button.getText())));
        }
        for(Node b:VButtons.getChildren()){
            Button button = (Button)b;
            button.setOnMousePressed((event -> marketAction(button.getText())));
        }
    }

    @FXML
    public void marketAction(String arrowID) {
        System.out.println(arrowID);
        //notifyObservers(new MarketActionEvent(Integer.parseInt(arrowID)));
    }

}

package it.polimi.ingsw.client.view.gui.controllers;

import it.polimi.ingsw.client.model.StorableResourceEnum;
import it.polimi.ingsw.client.view.gui.GraphicUtilities;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductionChoiceController {
    private final PersonalController personalController;
    @FXML private Button resource;
    @FXML private Button done;
    @FXML private AnchorPane mainPane;
    private Node production;

    public ProductionChoiceController(PersonalController personalController) {
        this.personalController = personalController;
    }
    @FXML
    public void initialize() {
        resource.setId(String.valueOf(0));
        resource.setOnMousePressed(event -> changeResourceAction(resource));
        done.setOnMousePressed(event -> doneAction());
    }

    private void doneAction() {
        String chosenResource = StorableResourceEnum.values()[Integer.parseInt(resource.getId())].toString();
        personalController.setChosenResource(chosenResource,production);
        Stage stage = (Stage) mainPane.getScene().getWindow();
        stage.close();
    }
    public void setProduction(Node n){
        production = n;
    }

    private void changeResourceAction(Button button){
        GraphicUtilities.loopResources(button);
    }
}

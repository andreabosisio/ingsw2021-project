package it.polimi.ingsw.client.view.gui.controllers;

import it.polimi.ingsw.client.view.gui.GraphicUtilities;
import it.polimi.ingsw.commons.enums.StorableResourceEnum;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * This class is used as the controller for the fxml scene:productionChoiceScene.fxml
 */
public class ProductionChoiceController {
    private final PersonalController personalController;
    @FXML
    Button resource;
    @FXML
    Button done;
    @FXML
    AnchorPane mainPane;
    @FXML
    Node production;

    public ProductionChoiceController(PersonalController personalController) {
        this.personalController = personalController;
    }

    /**
     * Function used to initialize the fxml when loaded
     * It loads the image of the first resource that can be produced in the resource button
     * It also sets changeResourceAction as the onMousePressed action of the resource button
     * and doneAction as the onMousePressed action of the done button
     */
    @FXML
    public void initialize() {
        resource.setId(String.valueOf(0));
        GraphicUtilities.loadResource(resource);
        resource.setOnMousePressed(event -> changeResourceAction(resource));
        done.setOnMousePressed(event -> doneAction());
    }

    /**
     * This method is called when the player presses the done button
     * It sets the chosen resource and the production in the personalController
     * It also closes this popup window
     */
    private void doneAction() {
        String chosenResource = StorableResourceEnum.values()[Integer.parseInt(resource.getId())].toString();
        personalController.setChosenResource(chosenResource, production);
        Stage stage = (Stage) mainPane.getScene().getWindow();
        stage.close();
    }

    /**
     * This method is used to set the node currently doing the production with choice
     *
     * @param n node doing the production
     */
    public void setProduction(Node n) {
        production = n;
    }

    /**
     * This method is called when the player presses the resource button
     * It changes the image in the imageView of the button to the image of the next resource in line
     *
     * @param button button containing the imageView to change
     */
    private void changeResourceAction(Button button) {
        GraphicUtilities.loopResources(button);
    }
}

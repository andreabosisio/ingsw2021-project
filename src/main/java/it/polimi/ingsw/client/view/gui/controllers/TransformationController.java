package it.polimi.ingsw.client.view.gui.controllers;

import it.polimi.ingsw.client.events.send.TransformationActionEvent;
import it.polimi.ingsw.client.view.gui.GUICommandListener;
import it.polimi.ingsw.client.view.gui.GraphicUtilities;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used as the controller for the fxml scene:whiteTransformation.fxml
 */
public class TransformationController extends GUICommandListener {
    private int numberOfTransformation;
    private List<String> possibleTransformations;
    private List<String> chosenResources;

    @FXML
    private AnchorPane mainPane;

    @FXML
    private HBox HTransformationBox;

    @FXML
    private Button transformer;

    @FXML
    private Button confirm;

    /**
     * This method is used to set the number and the possible colors of the transformations the player must perform
     *
     * @param numberOfTransformation  number of the resources to transform
     * @param possibleTransformations colors in which the resources can transform
     */
    public void setTransformation(int numberOfTransformation, List<String> possibleTransformations) {
        setNumberOfTransformation(numberOfTransformation);
        setPossibleTransformations(possibleTransformations);
    }

    /**
     * This method is used to set the number of resources the player must transform
     *
     * @param numberOfTransformation number of resources to transform
     */
    private void setNumberOfTransformation(int numberOfTransformation) {
        this.numberOfTransformation = numberOfTransformation;
    }

    /**
     * This method is used to set the color in which the white resources can transform
     *
     * @param possibleTransformations colors in which the resources can transform
     */
    private void setPossibleTransformations(List<String> possibleTransformations) {
        this.possibleTransformations = possibleTransformations;
    }

    /**
     * Function used to initialize the fxml when loaded
     * It set as enabled a number of transformation buttons equal to the number of resources to transform
     * It also set TransformationAction as the action confirm will perform on a mousePressed
     */
    @FXML
    private void initialize() {
        for (int i = 0; i < numberOfTransformation; i++) {
            VBox transformation = (VBox) HTransformationBox.getChildren().get(i);
            transformation.setDisable(false);
            transformation.setOpacity(1);
            int buttonIndex = transformation.getChildren().size() - 1;
            Button transformer = (Button) transformation.getChildren().get(buttonIndex);
            if (transformer != null) {
                transformer.setId(String.valueOf(0));
                GraphicUtilities.loadResource(transformer, possibleTransformations.get(0));
                transformer.setOnMousePressed((event -> changeResourceAction(transformer)));
            }
        }
        confirm.setOnMousePressed((event -> transformationAction()));
    }

    /**
     * This method is called when the player presses a transformation button
     * It sets the button id and imageView as the next resource in the list
     *
     * @param transformer button pressed b the player
     */
    private void changeResourceAction(Button transformer) {
        int index = Integer.parseInt(transformer.getId());
        index++;
        if (index >= possibleTransformations.size())
            index = 0;
        transformer.setId(String.valueOf(index));
        GraphicUtilities.loadResource(transformer, possibleTransformations.get(index));
    }

    /**
     * This method is called when the player presses the confirm button
     * It construct an event to send to the server containing the transformation chosen by the player
     * It also closes the transformation popup
     */
    private void transformationAction() {
        chosenResources = new ArrayList<>();
        for (Node transformation : HTransformationBox.getChildren()) {
            int buttonIndex = ((VBox) transformation).getChildren().size() - 1;
            Button transformer = (Button) ((VBox) transformation).getChildren().get(buttonIndex);
            if (transformer != null && !transformation.isDisable()) {
                chosenResources.add(possibleTransformations.get(Integer.parseInt(transformer.getId())));
            }
        }
        notifyObservers(new TransformationActionEvent(chosenResources));
        Stage stage = (Stage) mainPane.getScene().getWindow();
        stage.close();
    }
}

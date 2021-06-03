package it.polimi.ingsw.client.view.gui.controllers;

import it.polimi.ingsw.client.events.send.TransformationActionEvent;
import it.polimi.ingsw.client.view.gui.GUICommandListener;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

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

    public void setTransformation(int numberOfTransformation, List<String> possibleTransformations) {
        setNumberOfTransformation(numberOfTransformation);
        setPossibleTransformations(possibleTransformations);
    }

    private void setNumberOfTransformation(int numberOfTransformation) {
        this.numberOfTransformation = numberOfTransformation;
    }

    private void setPossibleTransformations(List<String> possibleTransformations) {
        this.possibleTransformations = possibleTransformations;
    }

    @FXML
    private void initialize() {
        for (int i = 0; i < numberOfTransformation; i++) {
            VBox transformation = (VBox) HTransformationBox.getChildren().get(i);
            transformation.setDisable(false);
            transformation.setOpacity(1);
            String id = transformation.getId();
            int buttonIndex = transformation.getChildren().size() - 1;
            Button transformer = (Button) transformation.getChildren().get(buttonIndex);
            if (transformer != null) {
                ((ImageView) transformer.getGraphic()).setImage(new Image(new File("src/main/resources/images/resources/" + possibleTransformations.get(0).toLowerCase(Locale.ROOT) + ".png").toURI().toString()));
                transformer.setOnMousePressed((event -> changeResourceAction(transformer)));
            }
        }
        confirm.setOnMousePressed((event -> transformationAction()));
    }

    private void changeResourceAction(Button transformer){
        int index = Integer.parseInt(transformer.getId());
        index++;
        if (index >= possibleTransformations.size())
            index = 0;
        transformer.setId(String.valueOf(index));
        File file = new File("src/main/resources/images/resources/" + possibleTransformations.get(index).toLowerCase(Locale.ROOT) + ".png");
        ImageView imageView = (ImageView) transformer.getGraphic();
        imageView.setImage(new Image(file.toURI().toString()));
        transformer.setGraphic(imageView);
    }

    private void transformationAction() {
        chosenResources = new ArrayList<>();
        for (Node transformation : HTransformationBox.getChildren()) {
            int buttonIndex = ((VBox) transformation).getChildren().size() - 1;
            Button transformer = (Button) ((VBox) transformation).getChildren().get(buttonIndex);
            if (transformer != null && !transformation.isDisable()) {
                chosenResources.add(possibleTransformations.get(Integer.parseInt(transformer.getId())));
            }
        }
        System.out.println(chosenResources);
        notifyObservers(new TransformationActionEvent(chosenResources));
        Stage stage = (Stage) mainPane.getScene().getWindow();
        stage.close();
    }
}

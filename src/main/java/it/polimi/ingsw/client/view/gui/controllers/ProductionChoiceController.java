package it.polimi.ingsw.client.view.gui.controllers;

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
    private PersonalController personalController;
    @FXML private Button resource;
    @FXML private Button done;
    @FXML private AnchorPane mainPane;
    private Node production;
    //todo switch to enum
    private final List<String> possibleResources = new ArrayList<>() {{
        add("YELLOW");
        add("BLUE");
        add("PURPLE");
        add("GRAY");
    }};
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
        String chosenResource = possibleResources.get(Integer.parseInt(resource.getId()));
        personalController.setChosenResource(chosenResource,production);
        Stage stage = (Stage) mainPane.getScene().getWindow();
        stage.close();
    }
    public void setProduction(Node n){
        production = n;
    }

    private void changeResourceAction(Button button){
        int number = Integer.parseInt(button.getId());
        number++;
        if(number>=possibleResources.size()){
            number = 0;
        }
        button.setId(String.valueOf(number));
        File file = new File("src/main/resources/images/resources/"+possibleResources.get(number).toLowerCase(Locale.ROOT)+".png");
        ImageView imageView = (ImageView) button.getGraphic();
        imageView.setImage(new Image(file.toURI().toString()));
        button.setGraphic(imageView);
    }
}

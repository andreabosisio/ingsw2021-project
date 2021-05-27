package it.polimi.ingsw.client.view.gui.controllers;

import it.polimi.ingsw.client.events.send.ChosenSetupEvent;
import it.polimi.ingsw.client.view.gui.GUI;
import it.polimi.ingsw.client.view.gui.GUICommandListener;
import it.polimi.ingsw.client.view.gui.GraphicUtilities;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class SetupController extends GUICommandListener {
    private int numberOfResources;
    private Stage marketWindow;
    private Stage gridWindow;

    private final List<String> possibleResources = new ArrayList<String>() {{
        add("YELLOW");
        add("BLUE");
        add("PURPLE");
        add("GRAY");
    }};
    private List<String> leaderCardsID;
    private List<Integer> chosenLeadersIndexes;
    private List<String> chosenResources;
    @FXML
    private Button done;
    @FXML
    private AnchorPane mainPane;
    @FXML
    private Button gridButton;
    @FXML
    private Button marketButton;
    @FXML
    private HBox HToggleLeaders;
    @FXML
    private HBox HToggleResources;
    @FXML
    public void initialize() {
        File file;
        int i = 0;
        for(String leaderID : leaderCardsID){
            ToggleButton toggleButton = (ToggleButton)HToggleLeaders.getChildren().get(i);
            toggleButton.setId(String.valueOf(i));
            file = new File("src/main/resources/images/leaders/"+leaderID+".png");
            ImageView imageView = (ImageView) toggleButton.getGraphic();
            imageView.setImage(new Image(file.toURI().toString()));
            toggleButton.setGraphic(imageView);
            i++;
        }
        for(i = 0;i<numberOfResources;i++){
            Button button =(Button)HToggleResources.getChildren().get(i);
            button.setId("0");
            button.setDisable(false);
            button.setOnMousePressed((event -> changeResourceAction(button)));
        }
        done.setOnMousePressed((event -> setupAction()));
        gridButton.setOnMousePressed((event -> seeGrid()));
        marketButton.setOnMousePressed((event -> seeMarket()));
    }
    public void initializeData(List<String> leaderCardsID, int numberOfResource){
        this.leaderCardsID = leaderCardsID;
        this.numberOfResources = numberOfResource;
    }
    private void setupAction(){
        chosenLeadersIndexes = new ArrayList<>();
        for(Node node:HToggleLeaders.getChildren()){
            ToggleButton button = (ToggleButton) node;
            if(button.isSelected()){
                try {
                    chosenLeadersIndexes.add(Integer.parseInt(button.getId()));
                }catch (NumberFormatException e){
                    printErrorMessage("Failed to extract index of chosen cards");
                }
            }
        }
        if(chosenLeadersIndexes.size()!=2){
            printErrorMessage("You must choose 2 Leaders");
            return;
        }
        chosenResources = new ArrayList<>();
        for(int i = 0;i<numberOfResources;i++){
            Button button =(Button)HToggleResources.getChildren().get(i);
            if(!button.isDisable()){
                chosenResources.add(possibleResources.get(Integer.parseInt(button.getId())));
            }
        }
        if(marketWindow !=null) {
            marketWindow.close();
        }
        if(gridWindow!=null){
            gridWindow.close();
        }
        System.out.println(chosenLeadersIndexes);
        System.out.println(chosenResources);
        notifyObservers(new ChosenSetupEvent(chosenLeadersIndexes,chosenResources));
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
    private void seeMarket(){
        Scene secondScene = null;
        FXMLLoader fxmlLoader = new FXMLLoader(GUI.class.getResource("/fxmls/marketScene.fxml"));
        fxmlLoader.setController(new MarketController(true));
        marketWindow = GraphicUtilities.populatePopupWindow(mainPane.getScene().getWindow(), fxmlLoader,marketWindow,Modality.NONE);
        marketWindow.show();
    }
    private void seeGrid(){
        FXMLLoader fxmlLoader = new FXMLLoader(GUI.class.getResource("/fxmls/devGridPopupScene.fxml"));
        gridWindow = GraphicUtilities.populatePopupWindow(mainPane.getScene().getWindow(), fxmlLoader,gridWindow,Modality.NONE);
        gridWindow.show();
    }
}



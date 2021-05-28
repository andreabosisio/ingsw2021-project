package it.polimi.ingsw.client.view.gui;

import it.polimi.ingsw.client.model.Board;
import it.polimi.ingsw.client.model.Marble;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class GraphicUtilities {

    private static final String marblesPath = "src/main/resources/images/marbles/";
    private static final String resourcesPath = "src/main/resources/images/resources/";
    private static final String devCardsPath = "src/main/resources/images/devCards/";
    private static final String leaderCardsPath = "src/main/resources/images/leaders/";
    private static final String endOfPath = ".png";

    //todo added this method to populate a market by giving grid with imageViews and extra imageView
    public static void populateMarket(GridPane marketToPopulate, ImageView extraRes) {
        //todo levare da qui e mettere nel metodo updateMarket di personal controller
        List<String> market = Board.getBoard().getMarketTray().toStringList();
        File file = new File(marblesPath + market.remove(0).toLowerCase(Locale.ROOT) + endOfPath);
        extraRes.setImage(new Image(file.toURI().toString()));
        ImageView temp;
        for (Node res : marketToPopulate.getChildren()) {
            file = new File(marblesPath + market.remove(0).toLowerCase(Locale.ROOT) + endOfPath);
            temp = (ImageView) res;
            temp.setImage(new Image(file.toURI().toString()));
        }
    }

    //todo added this method to populate a devGrid by giving grid with imageViews (from lvl3 to lvl1)
    public static void populateDevGrid(GridPane devGridToPopulate) {
        ImageView temp;
        File file;
        Button button;
        List<String> devGrid = Board.getBoard().getDevelopmentCardsGrid().toStringList();
        Collections.reverse(devGrid);
        for (Node card : devGridToPopulate.getChildren()) {
            button = (Button) card;
            button.setId(devGrid.get(0));
            temp = (ImageView) button.getGraphic();
            file = new File(devCardsPath + devGrid.remove(0).toLowerCase(Locale.ROOT) + endOfPath);
            temp.setImage(new Image(file.toURI().toString()));
        }
    }

    //todo added this method to update a devGrid by the iD of the new image
    public static void updateDevGrid(GridPane devGridPopulated, String iD) {
        ImageView temp;
        File file;
        Button button;
        int indexNewCard = 0;
        int counter = 0;

        List<String> devGrid = Board.getBoard().getDevelopmentCardsGrid().toStringList();
        Collections.reverse(devGrid);

        for (String devCardID : devGrid) {
            if (devCardID.equals(iD))
                break;
            else
                indexNewCard++;
        }
        for (Node card : devGridPopulated.getChildren()) {
            if (counter == indexNewCard) {
                button = (Button) card;
                temp = (ImageView) button.getGraphic();
                file = new File(devCardsPath + iD.toLowerCase(Locale.ROOT) + endOfPath);
                temp.setImage(new Image(file.toURI().toString()));
                break;
            }
            else
                counter++;
        }
    }

    public static void populateLeaders(HBox leadersBox, List<String> population) {
        if(leadersBox==null)
            return;
        File file;
        ImageView temp;
        for (Node leader : leadersBox.getChildren()) {
            file = new File(leaderCardsPath + population.remove(0).toLowerCase(Locale.ROOT) + endOfPath);
            temp = (ImageView) leader;
            temp.setImage(new Image(file.toURI().toString()));
        }
    }

    public static Stage populatePopupWindow(Window window, FXMLLoader fxmlLoader, Stage stageToPopulate, Modality modality) {
        Scene secondScene = null;
        try {
            secondScene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        // New window (Stage)
        if (stageToPopulate == null) {
            stageToPopulate = new Stage();
            stageToPopulate.setScene(secondScene);
            // Specifies the owner Window (parent) for new window
            stageToPopulate.initOwner(window);
            // Specifies the modality for new window.
            stageToPopulate.initModality(modality);
            // Set position of second window, related to primary window.
            stageToPopulate.setX(window.getX() + 200);
            stageToPopulate.setY(window.getY() + 100);
        }
        stageToPopulate.setScene(secondScene);
        return stageToPopulate;
    }

    public static void populateProductionBoard(AnchorPane personalBoard,String nickname){
        List<String> population = new ArrayList<>(Board.getBoard().getPersonalBoardOf(nickname).getProductionBoard());
        population.remove(0);//remove basic
        File file;
        ImageView temp;
        for(int i = 1;i<personalBoard.getChildren().size();i++){
            Button production = (Button) personalBoard.getChildren().get(i);
            ImageView cardImage = (ImageView) production.getGraphic();
            file = new File(devCardsPath + population.remove(0).toLowerCase(Locale.ROOT) + endOfPath);
            cardImage.setImage(new Image(file.toURI().toString()));
        }
    }

    public static void populateWarehouse(HBox fromMarket, AnchorPane warehouse, VBox leaders,GridPane strongbox, String nickname){
        List<Node> allDepotsAsNodes = new ArrayList<>();
        allDepotsAsNodes.addAll(fromMarket.getChildren());
        allDepotsAsNodes.addAll(warehouse.getChildren());
        allDepotsAsNodes.addAll(leaders.getChildren());
        allDepotsAsNodes.addAll(strongbox.getChildren());
        Map<Integer,String> warehouseMap =  Board.getBoard().getPersonalBoardOf(nickname).getWarehouse();
        File file;
        int i = 0;
        for(Node n:allDepotsAsNodes){
            Button button = (Button) n;
            ImageView resImage = (ImageView) button.getGraphic();
            String res = warehouseMap.getOrDefault(i, Marble.getEmptyResId());
            file = new File(resourcesPath + res.toLowerCase(Locale.ROOT) + endOfPath);
            resImage.setImage(new Image(file.toURI().toString()));
            i++;
        }
    }
}

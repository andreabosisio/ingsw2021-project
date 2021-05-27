package it.polimi.ingsw.client.view.gui;

import it.polimi.ingsw.client.model.Board;
import it.polimi.ingsw.client.view.gui.controllers.MarketController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class GraphicUtilities {

    private static final String resourcesPath = "src/main/resources/images/marbles/";
    private static final String devCardsPath = "src/main/resources/images/devCards/";
    private static final String leaderCardsPath = "src/main/resources/images/leaders/";
    private static final String endOfPath = ".png";

    //todo added this method to populate a market by giving grid with imageViews and extra imageView
    public static void populateMarket(GridPane marketToPopulate, ImageView extraRes) {
        List<String> market = Board.getBoard().getMarketTray().toStringList();
        File file = new File(resourcesPath + market.remove(0).toLowerCase(Locale.ROOT) + endOfPath);
        extraRes.setImage(new Image(file.toURI().toString()));
        ImageView temp;
        for (Node res : marketToPopulate.getChildren()) {
            file = new File(resourcesPath + market.remove(0).toLowerCase(Locale.ROOT) + endOfPath);
            temp = (ImageView) res;
            temp.setImage(new Image(file.toURI().toString()));
        }
    }

    //todo added this method to populate a devGrid by giving grid with imageViews (from lvl3 to lvl1)
    public static void populateDevGrid(GridPane devGridToPopulate) {
        ImageView temp;
        File file;
        List<String> devGrid = Board.getBoard().getDevelopmentCardsGrid().toStringList();
        Collections.reverse(devGrid);
        for (Node res : devGridToPopulate.getChildren()) {
            file = new File(devCardsPath + devGrid.remove(0).toLowerCase(Locale.ROOT) + endOfPath);
            temp = (ImageView) res;
            temp.setImage(new Image(file.toURI().toString()));
        }
    }

    public static void populateLeaders(HBox leadersBox, String nickname) {
        File file;
        ImageView temp;
        List<String> population = Board.getBoard().getPersonalBoardOf(nickname).getHandLeaders();
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
}

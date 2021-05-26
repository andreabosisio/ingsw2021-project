package it.polimi.ingsw.client.view.gui;

import it.polimi.ingsw.client.model.Board;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class GraphicUtilities {

    private static final String resourcesPath = "src/main/resources/images/marbles/";
    private static final String devCardsPath = "src/main/resources/images/devCards/";
    private static final String endOfPath = ".png";

    //todo added this method to populate a market by giving grid with imageViews and extra imageView
    public static void populateMarket(GridPane marketToPopulate,ImageView extraRes){
        List<String> market = Board.getBoard().getMarketTray().toStringList();
        File file = new File(resourcesPath+ market.remove(0).toLowerCase(Locale.ROOT)+ endOfPath);
        extraRes.setImage(new Image(file.toURI().toString()));
        ImageView temp;
        for(Node res:marketToPopulate.getChildren()){
            file = new File(resourcesPath+ market.remove(0).toLowerCase(Locale.ROOT)+endOfPath);
            temp = (ImageView)res;
            temp.setImage(new Image(file.toURI().toString()));
        }
    }
    //todo added this method to populate a devGrid by giving grid with imageViews (from lvl3 to lvl1)
    public static void populateDevGrid(GridPane devGridToPopulate){
        ImageView temp;
        File file;
        List<String> devGrid = Board.getBoard().getDevelopmentCardsGrid().toStringList();
        Collections.reverse(devGrid);
        for(Node res:devGridToPopulate.getChildren()){
            file = new File(devCardsPath+ devGrid.remove(0).toLowerCase(Locale.ROOT)+endOfPath);
            temp = (ImageView)res;
            temp.setImage(new Image(file.toURI().toString()));
        }
    }
}

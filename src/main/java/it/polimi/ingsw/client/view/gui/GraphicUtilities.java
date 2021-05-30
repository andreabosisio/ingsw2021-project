package it.polimi.ingsw.client.view.gui;

import it.polimi.ingsw.client.model.Board;
import it.polimi.ingsw.client.model.Marble;
import it.polimi.ingsw.client.model.PersonalBoard;
import it.polimi.ingsw.server.model.player.Player;
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
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class GraphicUtilities {

    private static final String marblesPath = "src/main/resources/images/marbles/";
    private static final String resourcesPath = "src/main/resources/images/resources/";
    private static final String devCardsPath = "src/main/resources/images/devCards/";
    private static final String leaderCardsPath = "src/main/resources/images/leaders/";
    private static final String faithMarkersPath = "src/main/resources/images/faithMarkers/";
    private static final String popeTilesPath = "src/main/resources/images/popeTiles/";
    private static final String endOfPath = ".png";
    private static final String lorenzo = "Lorenzo il Magnifico";

    public static String getAiName() {
        return lorenzo;
    }

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

    public static void populateLegend(AnchorPane legendPane) {
        List<String> colors = new ArrayList<String>() {{
            add("#ff0000");
            add("#0800ff");
            add("#00ff33");
            add("#ff00be");
        }};
        Map<String, Integer> faithTracks = Board.getBoard().getFaithTrack().getIndexes();
        int i = 0;
        for (String key : faithTracks.keySet()) {
            Button button = (Button) legendPane.getChildren().get(i);
            button.setText(key);
            button.setTextFill(Paint.valueOf(colors.get(i)));
            i++;
        }
        while (legendPane.getChildren().size() > i) {
            legendPane.getChildren().get(i).setVisible(false);
            i++;
        }
    }

    public static void populateFaithTracks(AnchorPane faithTrack) {
        ImageView temp;
        File file;

        Map<String, Integer> faithTracks = Board.getBoard().getFaithTrack().getIndexes();
        Map<String, Boolean[]> popeReports = Board.getBoard().getFaithTrack().getReports();

        Map<Integer, String> mapOfPlayers = new HashMap<>();

        int indexOfPlayer = 0;
        for (String key : faithTracks.keySet()) {
            mapOfPlayers.put(indexOfPlayer, key);
            indexOfPlayer++;
        }

        int index = 0;

        for (Node faithMarker : faithTrack.getChildren()) {
            if (index == (faithTracks.get(mapOfPlayers.get(0)) * 4)) {
                temp = (ImageView) faithMarker;
                file = new File(faithMarkersPath + "faithMarker0" + endOfPath);
                temp.setImage(new Image(file.toURI().toString()));
            } else if (index == (faithTracks.get(mapOfPlayers.get(1)) * 4 + 1)) {
                temp = (ImageView) faithMarker;
                file = new File(faithMarkersPath + "faithMarker1" + endOfPath);
                temp.setImage(new Image(file.toURI().toString()));
            } else if (faithTracks.size() > 2 && index == (faithTracks.get(mapOfPlayers.get(2)) * 4 + 2)) {
                temp = (ImageView) faithMarker;
                file = new File(faithMarkersPath + "faithMarker2" + endOfPath);
                temp.setImage(new Image(file.toURI().toString()));
            } else if (faithTracks.size() > 3 && index == (faithTracks.get(mapOfPlayers.get(3)) * 4 + 3)) {
                temp = (ImageView) faithMarker;
                file = new File(faithMarkersPath + "faithMarker3" + endOfPath);
                temp.setImage(new Image(file.toURI().toString()));
            } else {
                temp = (ImageView) faithMarker;
                temp.setImage(null);
            }
            index++;
        }
    }

    public static void populatePopeTiles(AnchorPane popeTiles, String nickname) {
        ImageView temp;
        File file;

        Boolean[] popeReports = Board.getBoard().getFaithTrack().getReports().get(nickname);

        int i = 0;
        for (Node popeTile : popeTiles.getChildren()) {
            if (popeReports[i]) {
                temp = (ImageView) popeTile;
                file = new File(popeTilesPath + "popeTile" + i + endOfPath);
                temp.setImage(new Image(file.toURI().toString()));
            }
            else {
                temp = (ImageView) popeTile;
                file = new File(popeTilesPath + "popeTileBack" + i + endOfPath);
                temp.setImage(new Image(file.toURI().toString()));
            }
            i++;
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
            } else
                counter++;
        }
    }

    public static void populateActiveLeaders(List<String> population, HBox leadersBox, VBox warehouseLeaderBox, HBox productionLeaderBox) {
        File file;
        ImageView temp;
        String leaderID;
        AtomicInteger wCount = new AtomicInteger(0);
        AtomicInteger pCount = new AtomicInteger(0);
        for (Node leader : leadersBox.getChildren()) {
            leaderID = population.remove(0).toLowerCase(Locale.ROOT);
            //check if leader is of type warehouse and in case activate assigned buttons
            if (leaderID.charAt(0) == 'w') {
                warehouseLeaderBox.getChildren().get(wCount.getAndIncrement()).setVisible(true);
                warehouseLeaderBox.getChildren().get(wCount.getAndIncrement()).setVisible(true);
            }
            if (leaderID.charAt(0) == 'p') {
                productionLeaderBox.getChildren().get(pCount.get()).setVisible(true);
                Button leaderProductionButton = (Button) productionLeaderBox.getChildren().get(pCount.getAndIncrement());
                temp = (ImageView) leaderProductionButton.getGraphic();
            } else {
                temp = (ImageView) leader;
            }
            file = new File(leaderCardsPath + leaderID + endOfPath);
            temp.setImage(new Image(file.toURI().toString()));
        }
    }

    public static void populateHandLeaders(HBox leadersBox, List<String> population) {
        //todo line below could be removed
        /*
        if (leadersBox == null)
            return;

         */


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

    public static void populateProductionBoard(AnchorPane personalBoard, String nickname) {
        List<String> population = new ArrayList<>(Board.getBoard().getPersonalBoardOf(nickname).getProductionBoard());
        population.remove(0);//remove basic
        File file;
        ImageView temp;
        for (Node node : personalBoard.getChildren()) {
            Button production = (Button) node;
            ImageView cardImage = (ImageView) production.getGraphic();
            file = new File(devCardsPath + population.remove(0).toLowerCase(Locale.ROOT) + endOfPath);
            cardImage.setImage(new Image(file.toURI().toString()));
        }
    }

    public static void populateWarehouse(HBox fromMarket, AnchorPane warehouse, VBox leaders, GridPane strongbox, String nickname) {
        List<Node> allDepotsAsNodes = new ArrayList<>();
        allDepotsAsNodes.addAll(fromMarket.getChildren());
        allDepotsAsNodes.addAll(warehouse.getChildren());
        allDepotsAsNodes.addAll(leaders.getChildren());
        allDepotsAsNodes.addAll(strongbox.getChildren());
        Map<Integer, String> warehouseMap = Board.getBoard().getPersonalBoardOf(nickname).getWarehouse();
        File file;
        int i = 0;
        for (Node n : allDepotsAsNodes) {
            Button button = (Button) n;
            ImageView resImage = (ImageView) button.getGraphic();
            String res = warehouseMap.getOrDefault(i, Marble.getEmptyResId());
            file = new File(resourcesPath + res.toLowerCase(Locale.ROOT) + endOfPath);
            resImage.setImage(new Image(file.toURI().toString()));
            i++;
        }
    }
}

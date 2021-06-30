package it.polimi.ingsw.client.view.gui;

import it.polimi.ingsw.client.model.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.Bloom;
import javafx.scene.control.ButtonBase;
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

public class GraphicUtilities {

    private static final String marblesPath = "src/main/resources/images/marbles/";
    private static final String resourcesPath = "src/main/resources/images/resources/";
    private static final String devCardsPath = "src/main/resources/images/devCards/";
    private static final String leaderCardsPath = "src/main/resources/images/leaders/";
    private static final String faithMarkersPath = "src/main/resources/images/faithMarkers/";
    private static final String popeTilesPath = "src/main/resources/images/popeTiles/";
    private static final String endOfPath = ".png";
    private static final String lorenzo = "Lorenzo il Magnifico";
    private static final String redCrossColor = "#ff0000";
    private static final String blueCrossColor = "#0800ff";
    private static final String greenCrossColor = "#00ff33";
    private static final String pinkCrossColor = "#ff00be";

    /**
     * Getter of the single player AI name
     *
     * @return AI name
     */
    public static String getAiName() {
        return lorenzo;
    }

    /**
     * Method used to populate a market as defined in the GUI:
     * A GridPane with 12 imageViews representing the marbles and a single imageView for the extra resource
     * It does so by asking the reduced model for data and loading appropriates images in the imageViews
     *
     * @param marketToPopulate The gridPane to populate
     * @param extraRes the imageView representing the extra slot
     */
    public static void populateMarket(GridPane marketToPopulate, ImageView extraRes) {
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

    /**
     * This method is used to populate the player legend in the GUI:
     * It does so by writing each player name in a button using the color associated with him;
     *
     * @param legendPane The Pane containing the buttons for the names
     */
    public static void populateLegend(AnchorPane legendPane) {
        List<String> colors = new ArrayList<>() {{
            add(redCrossColor);
            add(blueCrossColor);
            add(greenCrossColor);
            add(pinkCrossColor);
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

    /**
     * This method is used to populate the common faithTrack in the GUI:
     * It does so by putting the right faith marker in the appropriate imageView in the anchorPane
     *
     * @param faithTrack AnchorPane containing only imageViews as groups of 4 for each faith step
     */
    public static void populateFaithTracks(AnchorPane faithTrack) {
        ImageView temp;
        File file;
        Map<String, Integer> faithTracks = Board.getBoard().getFaithTrack().getIndexes();
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

    /**
     * Method used to populate the popeTiles in the GUI:
     *
     * @param popeTiles anchorPane containing three imageViews
     * @param nickname nickname of the player whose tiles must be populated
     */
    public static void populatePopeTiles(AnchorPane popeTiles, String nickname) {
        ImageView temp;
        File file;
        Boolean[] popeReports = Board.getBoard().getFaithTrack().getReports().get(nickname);
        int i = 0;
        for (Node popeTile : popeTiles.getChildren()) {
            temp = (ImageView) popeTile;
            if (popeReports[i]) {
                file = new File(popeTilesPath + "popeTile" + i + endOfPath);
            } else {
                file = new File(popeTilesPath + "popeTileBack" + i + endOfPath);
            }
            temp.setImage(new Image(file.toURI().toString()));
            i++;
        }
    }


    /**
     * This method is used to populate the developmentCardsGrid  in the GUI:
     * It does so by loading the right image in the associated imageView using the cardId for the path
     * and the reduced model for data.
     * The cards are loaded from lvl1 to lvl3
     *
     * @param devGridToPopulate GridPane containing 12 buttons representing cards
     */
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

    //Used to update a devGrid by the iD of the new image

    /**
     * This method is used to update the developmentCardsGrid in the GUI:
     * It does so by matching the modified ImageView with its new card and loading in it the new image.
     *
     * @param populatedDevelopmentGrid gridPane containing the developmentCardsGrid
     * @param iD ID of the new placed card
     */
    public static void updateDevGrid(GridPane populatedDevelopmentGrid, String iD) {
        if (iD.equals(DevelopmentCard.getEmptyCardID())) {
            populateDevGrid(populatedDevelopmentGrid);
            return;
        }
        ImageView cardSlot;
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
        for (Node card : populatedDevelopmentGrid.getChildren()) {
            if (counter == indexNewCard) {
                button = (Button) card;
                cardSlot = (ImageView) button.getGraphic();

                //fixme maybe remove
                Bloom bloom = new Bloom();
                for(int threshold = 100000; threshold >= 0; threshold--) {
                    cardSlot.setEffect(bloom);
                    bloom.setThreshold(threshold*0.00001);
                }

                file = new File(devCardsPath + iD.toLowerCase(Locale.ROOT) + endOfPath);
                cardSlot.setImage(new Image(file.toURI().toString()));
                cardSlot.setEffect(null);
                break;
            } else
                counter++;
        }
    }

    /**
     * This method is used to populate the activeLeaders in the GUI:
     * It does so by activating the javafx elements associated with each leaderCard id power
     *
     * @param population List of IDs representing the active leaderCards the player own
     * @param leadersBox HBox containing the images of active leaders
     * @param warehouseLeaderBox VBox containing the buttons for the extraSlots offered by some leaders
     * @param productionLeaderBox VBox containing the buttons for the extraProductionSlots offered by some leaders
     */
    public static void populateActiveLeaders(List<String> population, HBox leadersBox, VBox warehouseLeaderBox, HBox productionLeaderBox) {
        File file;
        ImageView temp;
        String leaderID;
        int i = 0;
        AtomicInteger wCount = new AtomicInteger(0);
        AtomicInteger pCount = new AtomicInteger(4);
        for (Node leader : leadersBox.getChildren()) {
            leaderID = population.remove(0).toLowerCase(Locale.ROOT);
            //check if leader is of type warehouse and in case activate assigned buttons
            if (leaderID.charAt(0) == 'w') {
                warehouseLeaderBox.getChildren().get(wCount.getAndIncrement()).setVisible(true);
                warehouseLeaderBox.getChildren().get(wCount.getAndIncrement()).setVisible(true);
            }
            if (leaderID.charAt(0) == 'p') {
                productionLeaderBox.getChildren().get(i).setVisible(true);
                Button leaderProductionButton = (Button) productionLeaderBox.getChildren().get(i);
                leaderProductionButton.setId(String.valueOf(pCount.getAndIncrement()));
                temp = (ImageView) leaderProductionButton.getGraphic();
            } else {
                temp = (ImageView) leader;
            }
            file = new File(leaderCardsPath + leaderID + endOfPath);
            temp.setImage(new Image(file.toURI().toString()));
            i++;
        }
    }

    /**
     * This method is used to populate the leader in hand in the GUI:
     * It does so by loading the leader images in the imageViews using the ID as path
     *
     * @param leadersBox HBox containing the imageViews where to load the leaders
     * @param population List of Ids of the leaders in hand to load
     */
    public static void populateHandLeaders(HBox leadersBox, List<String> population) {
        File file;
        ImageView temp;
        for (Node leader : leadersBox.getChildren()) {
            file = new File(leaderCardsPath + population.remove(0).toLowerCase(Locale.ROOT) + endOfPath);
            temp = (ImageView) leader;
            temp.setImage(new Image(file.toURI().toString()));
        }
    }

    /**
     * This method is used to populate a new or given stage using a given fxmlLoader,
     * It is then given a specified modality with a given window as its parent
     *
     * @param window parent of the new popup
     * @param fxmlLoader fxmlLoader containing the fxml to use
     * @param stageToPopulate Stage where the scene will be loaded(null to create a new stage)
     * @param modality relationship to set between the popup and its parent
     * @return the popup Stage created
     */
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

    /**
     * This method is used to populate the production board in the GUI:
     * It does so by loading the appropriate images in a AnchorPane containing only buttons following the reduced model data
     *
     * @param productionPane Pane containing the buttons representing the productions basic and leaders not included
     * @param nickname player owner of the productionBoard
     */
    public static void populateProductionBoard(AnchorPane productionPane, String nickname) {
        List<LinkedHashSet<String>> population = new ArrayList<>(Board.getBoard().getPersonalBoardOf(nickname).getDevelopmentCardsInSlots());
        population.remove(0);//remove basic power card
        File file;
        int i = 0;
        for (Node slotNode : productionPane.getChildren()) {
            AnchorPane slotPane = (AnchorPane) slotNode;
            List<Node> cardSlots = slotPane.getChildren();
            LinkedHashSet<String> slot = population.get(i);
            if (slot.size() > 1)
                slot.removeIf(id -> id.equals(DevelopmentCard.getEmptyCardID()));
            int j = cardSlots.size() - 1;
            String[] slotAsArray = slot.toArray(new String[0]);
            for (int k = slotAsArray.length - 1; k >= 0; k--) {
                ImageView cardImage;
                if (j == cardSlots.size() - 1)
                    cardImage = (ImageView) ((Button) cardSlots.get(j)).getGraphic();
                else
                    cardImage = (ImageView) cardSlots.get(j);
                file = new File(devCardsPath + slotAsArray[k].toLowerCase(Locale.ROOT) + endOfPath);
                cardImage.setImage(new Image(file.toURI().toString()));
                j--;
            }
            i++;
        }
    }

    /**
     * This method is used to populate the depots of a player in the GUI:
     * It does so by using the reduced model data to load the appropriate images in a list of buttons contained in multiple panes
     *
     * @param fromMarket HBox containing the buttons for the resources form market
     * @param warehouse AnchorPane containing the buttons for the resources in the warehouse
     * @param leaders VBox containing the buttons for the resources in the leaders special slots
     * @param strongbox GridPane containing the buttons for the resources in the strongBox
     * @param nickname nickname of the player owner of the depots
     */
    public static void populateDepots(HBox fromMarket, AnchorPane warehouse, VBox leaders, GridPane strongbox, String nickname) {
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

    /**
     * This method is used to load in a ImageView of a button containing a resource
     * the image of the next resource in line
     *
     * @param button button containing the imageView to change
     */
    public static void loopResources(Button button) {
        int number = Integer.parseInt(button.getId());
        number++;
        if(number >= StorableResourceEnum.values().length){
            number = 0;
        }
        button.setId(String.valueOf(number));
        loadResourceFromEnumIndex(button, number);
    }

    /**
     * This method is used to load in a ImageView of a button containing a resource
     * the image of the resource associated with the button current id
     *
     * @param button button containing the imageView to change
     */
    public static void loadResource(Button button){
        int number = Integer.parseInt(button.getId());
        loadResourceFromEnumIndex(button, number);
    }

    /**
     * This method is used to load in a ImageView of a button the image of the resource
     * associated with the given color;
     * @param button The button containing the imageView to change
     * @param color color of the resource to load in the imageView
     */
    public static void loadResource(Button button, String color){
        File file = new File(resourcesPath + color.toLowerCase(Locale.ROOT) + endOfPath);
        loadFileInButtonImageView(button,file);
    }

    /**
     * This method is used to load in a ImageView of a button the image of the resource
     * associated with the given id;
     *
     * @param button button containing the imageView to change
     * @param number color enum index of the resource to load
     */
    private static void loadResourceFromEnumIndex(Button button, int number) {
        File file = new File(resourcesPath + StorableResourceEnum.values()[number].toString().toLowerCase(Locale.ROOT)+endOfPath);
        loadFileInButtonImageView(button,file);
    }

    /**
     * This method is used to load a leader image in a button imageView using the ID as path
     *
     * @param button button where the image will be loaded
     * @param leaderID ID of the leader to load
     */
    public static void loadLeaderImage(ButtonBase button,String leaderID){
        File file = new File(leaderCardsPath + leaderID + endOfPath);
        loadFileInButtonImageView(button,file);
    }

    /**
     * This method is used to load a file into a button imageView
     *
     * @param button button where to load the file
     * @param file file to load
     */
    private static void loadFileInButtonImageView(ButtonBase button,File file){
        ImageView imageView = (ImageView) button.getGraphic();
        imageView.setImage(new Image(file.toURI().toString()));
        button.setGraphic(imageView);
    }
}

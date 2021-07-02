package it.polimi.ingsw.server.model.gameBoard;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.polimi.ingsw.commons.FileUtilities;
import it.polimi.ingsw.commons.Parser;
import it.polimi.ingsw.server.exceptions.InvalidIndexException;
import it.polimi.ingsw.server.model.resources.Resource;
import it.polimi.ingsw.server.model.resources.ResourceFactory;
import it.polimi.ingsw.server.model.turn.TurnLogic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class implements the Market Tray.
 * Arrow index implementation:
 * X X X X < 0
 * X X X X < 1
 * X X X X < 2
 * ^ ^ ^ ^
 * 6 5 4 3
 */
public class MarketTray {

    /**
     * Number of columns of the MarketBoard
     */
    public static final int NUM_C = 4;

    /**
     * Number of rows of the MarketBoard
     */
    public static final int NUM_R = 3;

    /**
     * Implementation of the MarketBoard
     */
    private final Resource[][] marketBoard = new Resource[NUM_R][NUM_C];

    /**
     * Extra resource of the line selection
     */
    private Resource extraSlot;

    /**
     * The current turn
     */
    private TurnLogic turn;

    private final List<Resource> initResources = new ArrayList<>();
    private List<Resource> tempNewResources = new ArrayList<>();

    /**
     * Create a Market Tray reading the initial Resources configuration contained in the file MARKET_INIT_RES_PATH.
     */
    public MarketTray() {
        loadResources(true);
        Collections.shuffle(initResources);
        populateMarket();
    }

    /**
     * Save the initial state in a file.
     */
    public void saveData() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("resources", Parser.toJsonTree(toStringList()));
        FileUtilities.writeJsonElementInFile(jsonObject, FileUtilities.SAVED_MARKET_DATA_PATH);
    }

    /**
     * Update the state of the MarketBoard with the Resources stored in initResources.
     */
    private void populateMarket() {
        int k = 1;
        for (int i = 0; i < NUM_R; i++) {
            for (int j = 0; j < NUM_C; j++) {
                try {
                    marketBoard[i][j] = initResources.get(k);
                    k++;
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            extraSlot = initResources.get(0);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    /**
     * Set the Market Resources reading a configuration file.
     *
     * @param reset true to load the initial configuration, false to re-load the saved configuration
     */
    private void loadResources(boolean reset) {
        JsonElement fileElement;
        initResources.clear();

        if(reset)
            fileElement = FileUtilities.getJsonElementFromFile(FileUtilities.MARKET_DEFAULT_CONFIG_PATH);
        else
            fileElement = FileUtilities.getJsonElementFromFile(FileUtilities.SAVED_MARKET_DATA_PATH);

        assert fileElement != null;
        JsonArray jsonArrayOfResources = Parser.extractFromField(fileElement,"resources").getAsJsonArray();
        for (JsonElement resource : jsonArrayOfResources) {//cycle through all resources
            initResources.add(ResourceFactory.produceInitialResource(resource.getAsString()));
        }
    }

    /**
     * Take the resources present in the chosen line and for each one call their own market ability.
     * Change the MarketBoard after the selection.
     *
     * @param arrow which represents the index of the chosen line: 0,1 and 2 for the rows and 3,4,5 and 6 for the column
     * @return the chosen resources
     * @throws InvalidIndexException if arrow < 0 or arrow > 6
     */
    public List<Resource> takeResources(int arrow) throws InvalidIndexException {
        tempNewResources.clear();
        Resource[] chosenLine;
        Resource temp;
        if (arrow >= 0) {
            if (arrow <= 2) { //if player chose a row
                chosenLine = new Resource[NUM_C];
                for (int i = 0; i < NUM_C; i++) {
                    chosenLine[i] = marketBoard[arrow][i];
                    chosenLine[i].marketAbility(turn);
                }
                temp = chosenLine[0];

                System.arraycopy(chosenLine, 1, marketBoard[arrow], 0, NUM_C - 1);
                marketBoard[arrow][NUM_C - 1] = extraSlot;
            } else if (arrow <= 6) { //if player chose a column
                chosenLine = new Resource[NUM_R];
                for (int i = 0; i < NUM_R; i++) {
                    chosenLine[i] = marketBoard[i][6 - arrow];
                    chosenLine[i].marketAbility(turn);
                }
                temp = chosenLine[0];
                for (int i = 0; i < NUM_R - 1; i++)
                    marketBoard[i][6 - arrow] = chosenLine[i + 1];
                marketBoard[NUM_R - 1][6 - arrow] = extraSlot;
            } else
                throw new InvalidIndexException("the arrow's id can't be > 6");
        } else
            throw new InvalidIndexException("the arrow's id can't be < 0");
        extraSlot = temp;

        return tempNewResources;
    }

    /**
     * This method add a resource
     *
     * @param toAdd resource to add
     * @return true if added successfully
     */
    public boolean addNewResource(Resource toAdd) {
        return this.tempNewResources.add(toAdd);
    }

    /**
     * Get the color of the resources contained in the Market sorted row-by-row in a List.
     * The first element is the extraSlot (the only Marble out of the matrix).
     *
     * @return the List described above
     */
    public List<String> toStringList() {
        List<String> toReturn = new ArrayList<>();
        toReturn.add(extraSlot.getColor().toString());
        for (int i = 0; i < NUM_R; i++)
            for (int j = 0; j < NUM_C; j++)
                toReturn.add(marketBoard[i][j].getColor().toString());
        return toReturn;
    }

    /**
     * Get the market board as a 2 dimensional array
     *
     * @return the market board
     */
    protected Resource[][] getMarketBoard() {
        return marketBoard;
    }

    /**
     * Getter for the resource contained in the extra slot
     *
     * @return Resource in the extra slot
     */
    protected Resource getExtraSlot() {
        return extraSlot;
    }

    /**
     * Set the turnLogic of the current game
     *
     * @param turn turnLogic of the current game
     */
    public void setTurn(TurnLogic turn) {
        this.turn = turn;
    }

    /**
     * Set the resources generated by a market action/or by a transformation of white resources
     *
     * @param tempNewResources List of resources to set
     */
    public void setTempNewResources(List<Resource> tempNewResources) {
        this.tempNewResources = tempNewResources;
    }

    /**
     * For testing
     *
     * @return tempNewResources
     */
    public List<Resource> getTempNewResources() {
        return tempNewResources;
    }

    /**
     * Reset the Market by giving an initial state.
     *
     * @param initResources The initial configuration of the Market
     */
    public void setNonRandom(List<Resource> initResources) {
        this.initResources.clear();
        this.initResources.addAll(initResources);
        populateMarket();
    }

    /**
     * Reset the Market to its initial state of the Game.
     */
    public void loadSavedData() {
        loadResources(false);
        populateMarket();
    }
}

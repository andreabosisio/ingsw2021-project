package it.polimi.ingsw.server.model.gameBoard;

import com.google.gson.*;
import it.polimi.ingsw.exceptions.InvalidIndexException;
import it.polimi.ingsw.server.model.resources.*;
import it.polimi.ingsw.server.model.turn.TurnLogic;

import java.io.*;
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

    private static final String MARKET_INIT_RES_PATH = "src/main/resources/market.json";

    /**
     * Number of columns of the MarketBoard
     */
    private static final int NUM_C = 4;

    /**
     * Number of rows of the MarketBoard
     */
    private static final int NUM_R = 3;

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
        loadResources();
        Collections.shuffle(initResources);
        populateMarket();
    }

    /**
     * Save the initial state in the in the file MARKET_INIT_RES_PATH.
     */
    public void saveData() {
        Gson gson = new Gson();
        try (FileWriter file = new FileWriter(MARKET_INIT_RES_PATH)) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("resources", gson.toJsonTree(toStringList()));
            gson.toJson(jsonObject, file);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Update the state of the MarketBoard with the Resources stored in initResources.
     */
    private void populateMarket() {
        int k = 0;
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
            extraSlot = initResources.get(k);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    /**
     * Read the Resources configuration saved in the file MARKET_INIT_RES_PATH.
     */
    private void loadResources() {
        initResources.clear();

        File input = new File(MARKET_INIT_RES_PATH);
        try {
            JsonElement fileElement = JsonParser.parseReader(new FileReader(input));
            JsonObject fileObject = fileElement.getAsJsonObject();
            JsonArray jsonArrayOfResources = fileObject.get("resources").getAsJsonArray();
            for (JsonElement resource : jsonArrayOfResources) {//cycle through all resources
                initResources.add(ResourceFactory.produceInitialResource(resource.getAsString()));
            }
        }
        catch (FileNotFoundException e) {
            System.err.println("file not found");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("error in the json file format");
            e.printStackTrace();
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

    protected Resource[][] getMarketBoard() {
        return marketBoard;
    }

    protected Resource getExtraSlot() {
        return extraSlot;
    }

    protected int getNUM_C() {
        return NUM_C;
    }

    protected int getNUM_R() {
        return NUM_R;
    }

    public void setTurn(TurnLogic turn) {
        this.turn = turn;
    }

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
        loadResources();
        populateMarket();
    }
}

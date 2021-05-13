package it.polimi.ingsw.server.model.gameBoard;

import it.polimi.ingsw.exceptions.InvalidIndexException;
import it.polimi.ingsw.server.model.enums.ResourceEnum;
import it.polimi.ingsw.server.model.resources.StorableResource;
import it.polimi.ingsw.server.model.resources.RedResource;
import it.polimi.ingsw.server.model.resources.Resource;
import it.polimi.ingsw.server.model.resources.WhiteResource;
import it.polimi.ingsw.server.model.turn.TurnLogic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
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
    private final int NUM_C = 4;

    /**
     * Number of rows of the MarketBoard
     */
    private final int NUM_R = 3;

    /**
     * Implementation of the MarketBoard
     */
    private final Resource[][] marketBoard = new Resource[NUM_R][NUM_C];

    /**
     * Extra resource of the line selection
     */
    private Resource extraSlot;

    private TurnLogic turn;


    /**
     * List of the resources available in the MarketBoard
     */
    private final List<Resource> initResources = new ArrayList<Resource>() {{
        add(new StorableResource(ResourceEnum.BLUE));
        add(new StorableResource(ResourceEnum.BLUE));
        add(new StorableResource(ResourceEnum.GRAY));
        add(new StorableResource(ResourceEnum.GRAY));
        add(new StorableResource(ResourceEnum.YELLOW));
        add(new StorableResource(ResourceEnum.YELLOW));
        add(new StorableResource(ResourceEnum.PURPLE));
        add(new StorableResource(ResourceEnum.PURPLE));
        add(new RedResource());
        add(new WhiteResource());
        add(new WhiteResource());
        add(new WhiteResource());
        add(new WhiteResource());
    }};

    private List<Resource> tempNewResources = new ArrayList<>();

    /**
     * Public constructor of the MarketTray.
     * Random initialization of the MarketBoard.
     */
    public MarketTray() {
        Collections.shuffle(initResources);
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

                /* replaced
                for(int i = 0; i < NUM_C - 1; i++)
                    marketBoard[arrow][i] = chosenLine[i + 1];
                */

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

        //System.out.println("--- INIT MARKET TRAY---");
        //tempNewResources.stream().forEach(rsc -> System.out.print(rsc.toString() + ", "));
        //System.out.println();
        //System.out.println("---END MARKET TRAY---");
        return tempNewResources;
    }

    public boolean addNewResource(Resource toAdd) {
        return this.tempNewResources.add(toAdd);
    }

    //todo cancellare
/*
    protected void printMarket(){
        System.out.println("                                        " + extraSlot.getColor());
        for(int i = 0; i < NUM_R; i++) {
            for (int j = 0; j < NUM_C; j++)
                System.out.print(marketBoard[i][j].getColor() + "       ");
            System.out.println("\n");
        }
    }

    protected void printMarket(Resource[][] market, Resource extraSlot){
        System.out.println("                                        " + extraSlot.getColor());
        for(int i = 0; i < NUM_R; i++) {
            for (int j = 0; j < NUM_C; j++)
                System.out.print(market[i][j].getColor() + "       ");
            System.out.println("\n");
        }

    }
 */

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
     * set state of market tray in a predetermined position
     */
    public void setNonRandom(List<Resource> resources) {
        initResources.clear();
        initResources.addAll(resources);
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

    //todo da cancellare
    /*
    public void print() {
        for (Resource[] resources : marketBoard) {
            for (Resource resource : resources) {
                System.out.print(resource.getColor().name() + " ");
            }
            System.out.print("\n");
        }
        System.out.println(extraSlot.getColor());
    }
     */
}

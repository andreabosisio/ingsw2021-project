package it.polimi.ingsw.client.model;

import it.polimi.ingsw.client.view.cli.Printable;
import it.polimi.ingsw.client.view.cli.PrintableScene;

import java.util.Set;

/**
 * This class implements the visible Board of the Game.
 */
public class Board extends Printable {
    private static Board instance;
    private MarketTray marketTray;
    private DevelopmentCardsGrid developmentCardsGrid;
    private FaithTrack faithTrack;
    private Set<PersonalBoard> personalBoards;

    private final static String MARKET_GRID_SEPARATOR = "            ";
    private final static String GRID_BOARD_SEPARATOR = "      |      ";

    /**
     * Create the single instance of this class.
     */
    private Board() {
    }

    /**
     * @return the only existing instance of the Board
     */
    public static synchronized Board getBoard() {
        if (instance == null)
            instance = new Board();
        return instance;
    }

    /**
     * Set the Market Tray.
     *
     * @param marketTray to set
     */
    public void setMarketTray(MarketTray marketTray) {
        this.marketTray = marketTray;
    }

    /**
     * Set the Development Cards Grid.
     *
     * @param developmentCardsGrid to set
     */
    public void setDevelopmentCardsGrid(DevelopmentCardsGrid developmentCardsGrid) {
        this.developmentCardsGrid = developmentCardsGrid;
    }

    /**
     * Set the Faith Track.
     *
     * @param faithTrack to set
     */
    public void setFaithTrack(FaithTrack faithTrack) {
        this.faithTrack = faithTrack;
    }

    /**
     * Set the Personal Boards of all the players.
     *
     * @param personalBoards to set
     */
    public void setPersonalBoards(Set<PersonalBoard> personalBoards) {
        this.personalBoards = personalBoards;
    }

    /**
     * @return the Market Tray
     */
    public MarketTray getMarketTray() {
        return marketTray;
    }

    /**
     * @return the Faith Track
     */
    public FaithTrack getFaithTrack() {
        return faithTrack;
    }

    /**
     * @return the Development Cards Grid
     */
    public DevelopmentCardsGrid getDevelopmentCardsGrid() {
        return developmentCardsGrid;
    }

    /**
     * @return all the Personal Boards
     */
    public Set<PersonalBoard> getAllPersonalBoards() {
        return personalBoards;
    }

    /**
     * Generates the Printable Scene containing the Market Tray and the Development Card Grids.
     *
     * @return the Printable Scene containing the Market Tray and the Development Card Grids
     */
    public Printable getPrintableMarketAndGrid() {
        Printable marketWithVerticalOffset = marketTray;
        for (int i = 0; i < developmentCardsGrid.getPrintable().size() / 2; i++)
            marketWithVerticalOffset = PrintableScene.addStringToTop(marketWithVerticalOffset, "");
        return PrintableScene.concatenatePrintables(MARKET_GRID_SEPARATOR, marketWithVerticalOffset, developmentCardsGrid);
    }

    /**
     * Generates the Printable Scene containing the Personal Board of the desired Player.
     *
     * @param nickname the nickname of the Player owner of the Personal Board
     * @return the Printable Scene containing the Personal Board of the desired Player
     */
    public PrintableScene getPrintablePersonalBoardOf(String nickname) {
        return new PrintableScene(PrintableScene.addPrintablesToTop(getPersonalBoardOf(nickname), 1, Board.getBoard().getFaithTrack().getFaithTrackWithLegendScene()));
    }

    /**
     * Generates the Printable Scene containing the Development Cards Grid, the Warehouse and the owned cards of the desired Player.
     *
     * @param nickname the nickname of the desired Player
     * @return the Printable Scene containing the Development Cards Grid, the Warehouse and the owned cards of the desired Player.
     */
    public PrintableScene getPrintableBuySceneOf(String nickname) {
        Printable warehouseAndCards = new PrintableScene(PrintableScene.addPrintablesToTop(getPersonalBoardOf(nickname).getWarehouseScene(), 1, getPersonalBoardOf(nickname).getActiveCardsScene()));
        return new PrintableScene(PrintableScene.concatenatePrintables(GRID_BOARD_SEPARATOR, developmentCardsGrid, warehouseAndCards));
    }

    /**
     * Generates the Printable Scene for the placement of a new Card.
     *
     * @param nickname      of the owner of the new Card
     * @param cardToPlaceID the Card to place
     * @return the Printable Scene  for the placement of a new Card.
     */
    public PrintableScene getPrintableCardPlacementSceneOf(String nickname, String cardToPlaceID) {
        return new PrintableScene(PrintableScene.addPrintablesToTop(getPersonalBoardOf(nickname).getActiveCardsScene(), 2, DevelopmentCardsDatabase.getDevelopmentCardsDatabase().createDevelopmentCardByID(cardToPlaceID)));
    }

    /**
     * Get the Personal Board of the desired Player.
     *
     * @param nickname of the desired Player
     * @return the Personal Board of the desired Player if exists, else return null.
     */
    public PersonalBoard getPersonalBoardOf(String nickname) {
        return personalBoards.stream().filter(p -> p.getNickname().equals(nickname)).findFirst().orElse(null);
    }
}

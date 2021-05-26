package it.polimi.ingsw.client.model;

import it.polimi.ingsw.client.view.cli.PrintableScene;
import it.polimi.ingsw.client.view.cli.Printable;

import java.util.Set;

public class Board extends Printable {
    private static Board instance;
    private MarketTray marketTray;
    private DevelopmentCardsGrid developmentCardsGrid;
    private FaithTrack faithTrack;
    private Set<PersonalBoard> personalBoards;

    private final static String MARKET_GRID_SEPARATOR = "            ";
    private final static String GRID_BOARD_SEPARATOR = "      |      ";

    private static int DIM_SCENE = 30;

    private Board() {
    }

    public static synchronized Board getBoard() {
        if (instance == null)
            instance = new Board();
        return instance;
    }

    public void setMarketTray(MarketTray marketTray) {
        this.marketTray = marketTray;
    }

    public void setDevelopmentCardsGrid(DevelopmentCardsGrid developmentCardsGrid) {
        this.developmentCardsGrid = developmentCardsGrid;
    }

    public void setFaithTrack(FaithTrack faithTrack) {
        this.faithTrack = faithTrack;
    }

    public void setPersonalBoards(Set<PersonalBoard> personalBoards) {
        this.personalBoards = personalBoards;
    }

    public MarketTray getMarketTray() {
        return marketTray;
    }

    public FaithTrack getFaithTrack() {
        return faithTrack;
    }

    public DevelopmentCardsGrid getDevelopmentCardsGrid() {
        return developmentCardsGrid;
    }

    public Set<PersonalBoard> getAllPersonalBoards() {
        return personalBoards;
    }

    public PrintableScene getPrintableMarketAndGrid() {
        Printable marketWithVerticalOffset = marketTray;
        for (int i = 0; i < developmentCardsGrid.getPrintable().size()/2; i++)
            marketWithVerticalOffset = PrintableScene.addStringToTop(marketWithVerticalOffset, "");
        return PrintableScene.concatenatePrintable(MARKET_GRID_SEPARATOR, marketWithVerticalOffset, developmentCardsGrid);
    }

    public PrintableScene getPrintablePersonalBoardOf(String nickname) {
        return new PrintableScene(PrintableScene.addPrintablesToTop(getPersonalBoardOf(nickname),1,  Board.getBoard().getFaithTrack().getFaithTrackWithLegendScene()));
    }

    public PrintableScene getPrintableBuySceneOf(String nickname) {
        Printable warehouseAndCards = new PrintableScene(PrintableScene.addPrintablesToTop(getPersonalBoardOf(nickname).getWarehouseScene(), 1 ,getPersonalBoardOf(nickname).getActiveCardsScene()));
        return new PrintableScene(PrintableScene.concatenatePrintable(GRID_BOARD_SEPARATOR, developmentCardsGrid, warehouseAndCards));
    }

    public PrintableScene getPrintableCardPlacementSceneOf(String nickname, String cardToPlaceID) {
        return new PrintableScene(PrintableScene.addPrintablesToTop(getPersonalBoardOf(nickname).getActiveCardsScene(), 2, DevelopmentCardsDatabase.getDevelopmentCardsDatabase().createDevelopmentCardByID(cardToPlaceID)));
    }

    @Override
    public int getWidth() {
        //fixme
        return DIM_SCENE;
    }
    public PersonalBoard getPersonalBoardOf(String nickname){
        return personalBoards.stream().filter(p -> p.getNickname().equals(nickname)).findFirst().orElse(null);
    }
}

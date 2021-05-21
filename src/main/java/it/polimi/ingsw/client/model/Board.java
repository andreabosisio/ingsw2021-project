package it.polimi.ingsw.client.model;

import it.polimi.ingsw.client.view.cli.PrintableScene;
import it.polimi.ingsw.client.view.cli.Printable;

import java.util.Set;

public class Board extends Printable {
    private static Board instance;
    private MarketTray marketTray;
    private DevelopmentCardsGrid developmentCardsGrid;
    private FaithTrack faithTrack;
    private Set<Player> players;

    private static String MARKET_GRID_SEPARATOR = "          ";

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

    public void setPlayers(Set<Player> players) {
        this.players = players;
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

    public Set<Player> getPlayers() {
        return players;
    }

    public Printable getPrintableMarketAndGrid() {
        Printable marketWithVerticalOffset = marketTray;
        for (int i = 0; i < developmentCardsGrid.getPrintable().size()/2; i++)
            marketWithVerticalOffset = PrintableScene.addStringToTop(marketWithVerticalOffset, "");
        return PrintableScene.concatenatePrintable(MARKET_GRID_SEPARATOR, marketWithVerticalOffset, developmentCardsGrid);
    }

    public Printable getPrintablePersonalBoardOf(String nickname) {
        return new PrintableScene(PrintableScene.addPrintablesToTop(getPlayerByNickname(nickname), Board.getBoard().getFaithTrack()));
    }

    public Printable getPrintableBuySceneOf(String nickname) {
        return new PrintableScene(PrintableScene.concatenatePrintable("   ", getPlayerByNickname(nickname).getWarehouseScene(), developmentCardsGrid));
    }

    @Override
    public int getWidth() {
        //fixme
        return DIM_SCENE;
    }
    public Player getPlayerByNickname(String nickname){
        return players.stream().filter(p->p.getNickname().equals(nickname)).findFirst().orElse(null);
    }
}

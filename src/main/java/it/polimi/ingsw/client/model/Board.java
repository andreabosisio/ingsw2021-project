package it.polimi.ingsw.client.model;

import it.polimi.ingsw.client.view.cli.PrintableScene;
import it.polimi.ingsw.client.view.cli.Printable;

import java.util.ArrayList;
import java.util.List;
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
        List<String> toReturn = new ArrayList<>();
        List<String> printableMarket = marketTray.getPrintable();
        List<String> printableGrid = developmentCardsGrid.getPrintable();
        StringBuilder row;
        String marketRow, gridRow;

        /*
        for(int i = 0; i < DIM_SCENE; i++) {
            row = new StringBuilder();
            try {
                marketRow = printableMarket.get(i);
            } catch (IndexOutOfBoundsException e) {
                marketRow = marketTray.getEmptySpace();
            }
            row.append(marketRow);

            row.append(MARKET_GRID_SEPARATOR);

            try {
                gridRow = printableGrid.get(i);
            } catch (IndexOutOfBoundsException e) {
                gridRow = developmentCardsGrid.getEmptySpace();
            }
            row.append(gridRow);
            toReturn.add(row.toString());
        }

        setWidth(toReturn);
        return new PrintableScene(toReturn);

         */
        Printable marketWithVerticalOffset = marketTray;
        for (int i = 0; i < developmentCardsGrid.getPrintable().size()/2; i++)
            marketWithVerticalOffset = PrintableScene.addTopString(marketWithVerticalOffset, "");
        return PrintableScene.concatenatePrintable(MARKET_GRID_SEPARATOR, marketWithVerticalOffset, developmentCardsGrid);
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

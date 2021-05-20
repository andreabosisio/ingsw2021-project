package it.polimi.ingsw.client.model;

import it.polimi.ingsw.client.model.DevelopmentCardsGrid;
import it.polimi.ingsw.client.model.MarketTray;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Board {
    private static Board instance;
    private MarketTray marketTray;
    private DevelopmentCardsGrid developmentCardsGrid;
    private FaithTrack faithTrack;
    private Set<Player> players;

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
}

package it.polimi.ingsw.server.events.send.graphics;

import it.polimi.ingsw.server.model.gameBoard.GameBoard;

import java.util.List;

/**
 * Contains the data for an update of the Market Tray.
 */
public class MarketUpdate {
    private final List<String> market;

    /**
     * Create a new Market Update containing all the Resource colors in it.
     */
    public MarketUpdate() {
        market = GameBoard.getGameBoard().getMarketTray().toStringList();
    }
}

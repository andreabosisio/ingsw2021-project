package it.polimi.ingsw.server.events.send.graphics;

import it.polimi.ingsw.server.model.gameBoard.GameBoard;

import java.util.List;

public class MarketUpdate extends GraphicsUpdateEvent{
    private final List<String> market;

    public MarketUpdate() {
        super("market");
        market = GameBoard.getGameBoard().getMarketTray().toStringList();
    }
}

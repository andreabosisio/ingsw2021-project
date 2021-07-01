package it.polimi.ingsw.client.events.send;

import it.polimi.ingsw.client.utils.ClientParser;

/**
 * Represent the request of a Market Action by a Player.
 */
public class MarketActionEvent extends EventToServer {
    private final int arrowID;

    /**
     * This event is sent when a Player chooses an arrow of the Market
     *
     * @param arrowID is the ID of the arrow chosen
     */
    public MarketActionEvent(int arrowID) {
        super(ClientParser.MARKET_ACTION_TYPE);
        this.arrowID = arrowID;
    }

}

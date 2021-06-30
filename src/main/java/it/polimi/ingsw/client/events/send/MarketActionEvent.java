package it.polimi.ingsw.client.events.send;

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
        super("marketAction");
        this.arrowID = arrowID;
    }

}

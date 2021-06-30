package it.polimi.ingsw.client.events.send;

/**
 * Represent the request of a Leader Action of a Player.
 */
public class LeaderActionEvent extends EventToServer {
    private final String leaderCardID;
    private final boolean discardCard;

    /**
     * This event is sent when a Player discards or activates a Leader Card
     *
     * @param leaderCardID the ID of the Leader Card
     * @param discardCard  is true if the card is discarded
     */
    public LeaderActionEvent(String leaderCardID, boolean discardCard) {
        super("leaderAction");
        this.discardCard = discardCard;
        this.leaderCardID = leaderCardID;
    }
}

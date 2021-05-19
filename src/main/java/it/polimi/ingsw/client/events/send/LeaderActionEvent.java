package it.polimi.ingsw.client.events.send;

public class LeaderActionEvent extends SendEvent{
    private final String leaderCardID;
    private final boolean discardCard;
    public LeaderActionEvent(String leaderCardID, boolean discardCard) {
        super("leaderAction");
        this.discardCard = discardCard;
        this.leaderCardID = leaderCardID;
    }
}

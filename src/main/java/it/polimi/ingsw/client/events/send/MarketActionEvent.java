package it.polimi.ingsw.client.events.send;

public class MarketActionEvent extends SendEvent{
    private final int arrowID;
    public MarketActionEvent(int arrowID) {
        super("marketAction");
        this.arrowID = arrowID;
    }

}

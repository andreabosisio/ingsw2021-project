package it.polimi.ingsw.client.events.send;

import java.util.List;

public class BuyActionEvent extends SendEvent{
    private final String cardColor;
    private final int cardLevel;
    private final List<Integer> resourcePositions;
    public BuyActionEvent(String cardColor, int cardLevel, List<Integer> resourcePositions) {
        super("buyAction");
        this.cardColor = cardColor;
        this.cardLevel = cardLevel;
        this.resourcePositions = resourcePositions;
    }
}

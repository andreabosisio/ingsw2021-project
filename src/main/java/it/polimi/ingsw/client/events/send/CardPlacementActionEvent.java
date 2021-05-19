package it.polimi.ingsw.client.events.send;

public class CardPlacementActionEvent extends SendEvent{
    private final int slotPosition;
    public CardPlacementActionEvent(int slotPosition) {
        super("cardPlacementAction");
        this.slotPosition=slotPosition;
    }
}

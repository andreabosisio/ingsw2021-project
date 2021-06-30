package it.polimi.ingsw.client.events.send;

/**
 * Represent the request of the End Turn of a Player.
 */
public class EndTurnActionEvent extends SendEvent{
    public EndTurnActionEvent() {
        super("endTurnAction");
    }
}

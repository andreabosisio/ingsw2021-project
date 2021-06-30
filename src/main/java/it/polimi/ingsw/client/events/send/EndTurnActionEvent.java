package it.polimi.ingsw.client.events.send;

/**
 * Represent the request of an End Turn Action by a Player.
 */
public class EndTurnActionEvent extends EventToServer {
    public EndTurnActionEvent() {
        super("endTurnAction");
    }
}

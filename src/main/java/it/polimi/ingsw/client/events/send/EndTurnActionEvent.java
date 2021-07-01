package it.polimi.ingsw.client.events.send;

import it.polimi.ingsw.client.utils.ClientParser;

/**
 * Represent the request of an End Turn Action by a Player.
 */
public class EndTurnActionEvent extends EventToServer {
    public EndTurnActionEvent() {
        super(ClientParser.END_TURN_ACTION_TYPE);
    }
}

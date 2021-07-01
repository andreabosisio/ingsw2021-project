package it.polimi.ingsw.server.events.send;

import it.polimi.ingsw.commons.Parser;

/**
 * Abstract Implementation of a general Event that has to be sent to the Client.
 */
public abstract class EventToClient {
    /**
     * Check if this event should be received by the nickname with this player.
     *
     * @param nickname to check
     * @return true if the receiver's nickname of this Event is the same of the given nickname
     */
    public boolean isForYou(String nickname) {
        return true;
    }

    /**
     * Transform this EventToServer to a String containing a JSON message.
     *
     * @return the produced String
     */
    public String toJson() {
        return Parser.toJson(this);
    }
}
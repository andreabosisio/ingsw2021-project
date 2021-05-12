package it.polimi.ingsw.server.events.send;

public interface SendEvent {
    /**
     * Check if the receiver's nickname of this Event is the same of the given nickname.
     *
     * @param nickname to check
     * @return true if the receiver's nickname of this Event is the same of the given nickname
     */
    boolean isForYou(String nickname);

    /**
     * Transform this SendEvent to a String containing a JSON message.
     *
     * @return the produced String
     */
    String toJson();
}
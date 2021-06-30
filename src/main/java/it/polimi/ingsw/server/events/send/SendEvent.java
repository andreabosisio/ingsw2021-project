package it.polimi.ingsw.server.events.send;

import com.google.gson.Gson;

public abstract class SendEvent {
    /**
     * Check if this event should be received by the nickname with this player.
     *
     * @param nickname to check
     * @return true if the receiver's nickname of this Event is the same of the given nickname
     */
    public boolean isForYou(String nickname){
        return true;
    }

    /**
     * Transform this SendEvent to a String containing a JSON message.
     *
     * @return the produced String
     */
    public String toJson(){
        return new Gson().toJson(this);
    }
}
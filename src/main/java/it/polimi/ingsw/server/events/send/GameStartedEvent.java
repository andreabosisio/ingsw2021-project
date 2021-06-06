package it.polimi.ingsw.server.events.send;

import com.google.gson.Gson;

import java.util.List;

public class GameStartedEvent implements SendEvent{
    private final String type = "gameStarted";
    private final List<String> nicknames;

    public GameStartedEvent(List<String> nicknames) {
        this.nicknames = nicknames;
    }

    /**
     * Check if the receiver's nickname of this Event is the same of the given nickname.
     *
     * @param nickname to check
     * @return true if the receiver's nickname of this Event is the same of the given nickname
     */
    @Override
    public boolean isForYou(String nickname) {
        return true;
    }

    /**
     * Transform this SendEvent to a String containing a JSON message.
     *
     * @return the produced String
     */
    @Override
    public String toJson() {
        return new Gson().toJson(this);
    }
}

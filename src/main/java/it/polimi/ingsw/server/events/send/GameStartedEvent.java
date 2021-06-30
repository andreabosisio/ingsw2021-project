package it.polimi.ingsw.server.events.send;

import com.google.gson.Gson;

import java.util.List;

public class GameStartedEvent extends SendEvent{
    private final String type = "gameStarted";
    private final List<String> nicknames;

    public GameStartedEvent(List<String> nicknames) {
        this.nicknames = nicknames;
    }
}

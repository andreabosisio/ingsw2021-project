package it.polimi.ingsw.server.events.send;

import java.util.List;

public class GameStartedEvent extends EventToClient {
    private final String type = "gameStarted";
    private final List<String> nicknames;

    public GameStartedEvent(List<String> nicknames) {
        this.nicknames = nicknames;
    }
}

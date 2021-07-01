package it.polimi.ingsw.server.events.send;

import it.polimi.ingsw.server.utils.ServerParser;

import java.util.List;

public class GameStartedEvent extends EventToClient {
    private final String type = ServerParser.GAME_STARTED_TYPE;
    private final List<String> nicknames;

    public GameStartedEvent(List<String> nicknames) {
        this.nicknames = nicknames;
    }
}

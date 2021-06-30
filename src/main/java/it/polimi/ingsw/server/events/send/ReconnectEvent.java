package it.polimi.ingsw.server.events.send;

import it.polimi.ingsw.server.events.send.graphics.GraphicUpdateEvent;
import it.polimi.ingsw.server.utils.ServerParser;

import java.util.List;

public class ReconnectEvent extends EventToClient {
    private final String type = ServerParser.reconnectType;
    private final String reconnectingPlayer;
    private final String currentPlayer;
    private final List<String> nicknames;
    private final GraphicUpdateEvent graphicUpdateEvent;

    public ReconnectEvent(String reconnectingPlayer, String currentPlayer, List<String> nicknames, GraphicUpdateEvent graphicUpdateEvent) {
        this.reconnectingPlayer = reconnectingPlayer;
        this.currentPlayer = currentPlayer;
        this.nicknames = nicknames;
        this.graphicUpdateEvent = graphicUpdateEvent;
    }

    @Override
    public boolean isForYou(String nickname) {
        return nickname.equals(reconnectingPlayer);
    }
}

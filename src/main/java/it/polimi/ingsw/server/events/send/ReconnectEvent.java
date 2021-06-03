package it.polimi.ingsw.server.events.send;

import com.google.gson.Gson;
import it.polimi.ingsw.server.events.send.graphics.GraphicUpdateEvent;

import java.util.List;

public class ReconnectEvent implements SendEvent {
    private final String type = "reconnect";
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

    @Override
    public String toJson() {
        return new Gson().toJson(this);
    }
}

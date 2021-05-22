package it.polimi.ingsw.server.events.send;

import com.google.gson.Gson;
import it.polimi.ingsw.server.events.send.graphics.GraphicUpdateEvent;
import it.polimi.ingsw.server.model.PlayerInterface;
import it.polimi.ingsw.server.model.player.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EndGameEvent implements SendEvent{
    private final String type = "endGame";
    private final Map<String, Integer> playersPoints = new HashMap<>();
    private final String winner;

    public EndGameEvent(PlayerInterface winner, List<Player> players) {
        this.winner = winner.getNickname();
        players.forEach(player -> playersPoints.put(player.getNickname(), player.getPersonalBoard().getPoints(player)));
    }

    @Override
    public boolean isForYou(String nickname) {
        return true;
    }

    @Override
    public String toJson() {
        return new Gson().toJson(this);
    }
}

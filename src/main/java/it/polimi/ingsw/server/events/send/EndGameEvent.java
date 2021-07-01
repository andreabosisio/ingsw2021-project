package it.polimi.ingsw.server.events.send;

import it.polimi.ingsw.server.model.PlayerInterface;
import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.server.utils.ServerParser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represent the event of the End of the Game.
 */
public class EndGameEvent extends EventToClient {
    private final String type = ServerParser.END_GAME_TYPE;
    private final Map<String, Integer> playersPoints = new HashMap<>();
    private final String winner;

    /**
     * Create a End Game Event.
     *
     * @param players All Players in game
     * @param winner The game's winner
     */
    public EndGameEvent(PlayerInterface winner, List<Player> players) {
        this.winner = winner.getNickname();
        players.forEach(player -> playersPoints.put(player.getNickname(), player.getPersonalBoard().getPoints(player)));
    }
}

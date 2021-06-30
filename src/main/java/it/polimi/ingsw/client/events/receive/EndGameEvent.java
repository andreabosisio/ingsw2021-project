package it.polimi.ingsw.client.events.receive;

import it.polimi.ingsw.client.view.View;

import java.util.Map;

/**
 * Represent the Event of End Game that contains the winner and the Victory Points of all the Players
 */
public class EndGameEvent implements EventFromServer {
    private final Map<String, Integer> playersPoints;
    private final String winner;
    public EndGameEvent(Map<String, Integer> playersPoints, String winner) {
        this.playersPoints = playersPoints;
        this.winner = winner;
    }

    @Override
    public void updateView(View view) {
        view.setOnEndGame(winner,playersPoints);
    }
}

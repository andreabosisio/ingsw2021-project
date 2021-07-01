package it.polimi.ingsw.client.events.receive;

import it.polimi.ingsw.client.view.View;

/**
 * Represent a Start Turn Event that starts the turn of the current Player
 * and set the others in "Wait For Your Turn"
 */
public class StartTurnUpdateEvent implements EventFromServer {
    private String nextPlayer;

    @Override
    public void updateView(View view) {
        if (view.getNickname().equals(nextPlayer)) {
            view.setIsPlaying(true);
            view.printInfoMessage("Your turn is starting ");
            view.showWaitAnimation();
            view.setOnYourTurn();
        } else {
            view.setIsPlaying(false);
            view.setOnWaitForYourTurn(nextPlayer);
        }
    }
}

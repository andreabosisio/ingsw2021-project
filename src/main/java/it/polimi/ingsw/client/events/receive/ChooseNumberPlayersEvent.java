package it.polimi.ingsw.client.events.receive;

import it.polimi.ingsw.client.view.View;

/**
 * Represent the request of the number of Players of the Game
 */
public class ChooseNumberPlayersEvent implements EventFromServer {

    private final String payload;

    /**
     * This is the event received when the player must decide the size of the lobby
     *
     * @param payload String equals to "between a and b" with a e b the min and max size for the lobby
     */
    public ChooseNumberPlayersEvent(String payload) {
        this.payload = payload;
    }

    @Override
    public void updateView(View view) {
        view.setOnChooseNumberOfPlayers(payload);
    }
}

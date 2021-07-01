package it.polimi.ingsw.client.events.receive;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.polimi.ingsw.client.model.Board;
import it.polimi.ingsw.client.model.PersonalBoard;
import it.polimi.ingsw.client.view.View;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Represent an Event of Reconnection: it contains all the information to update the state of the Player
 */
public class ReconnectEvent implements EventFromServer {
    private List<String> nicknames;
    private String currentPlayer;
    private String reconnectingPlayer;
    private JsonObject graphicUpdateEvent;

    @Override
    public void updateView(View view) {
        Board.getBoard().setPersonalBoards(nicknames.stream().map(PersonalBoard::new).collect(Collectors.toSet()));
        new Gson().fromJson(graphicUpdateEvent, GraphicUpdateEvent.class).updateView(view);
        view.setNickname(reconnectingPlayer);
        view.setOnWaitForYourTurn(currentPlayer);
    }
}

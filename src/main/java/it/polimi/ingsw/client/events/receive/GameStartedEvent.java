package it.polimi.ingsw.client.events.receive;

import it.polimi.ingsw.client.model.Board;
import it.polimi.ingsw.client.model.PersonalBoard;
import it.polimi.ingsw.client.view.View;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Represent the Event of Start Game that contains the nickname of all the Players
 */
public class GameStartedEvent implements EventFromServer {

    private final List<String> nicknames;

    public GameStartedEvent(List<String> nicknames) {
        this.nicknames = nicknames;
    }

    @Override
    public void updateView(View view) {
        Board.getBoard().setPersonalBoards(nicknames.stream().map(PersonalBoard::new).collect(Collectors.toSet()));
    }
}

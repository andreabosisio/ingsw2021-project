package it.polimi.ingsw.client.events.receive;

import it.polimi.ingsw.client.model.Board;
import it.polimi.ingsw.client.model.Player;
import it.polimi.ingsw.client.view.View;

import java.util.List;
import java.util.stream.Collectors;

public class GameStartedEvent implements ReceiveEvent{

    private final List<String> nicknames;

    public GameStartedEvent(List<String> nicknames) {
        this.nicknames = nicknames;
    }

    @Override
    public void updateView(View view) {
        //todo add server msg with all players
        Board.getBoard().setPlayers(nicknames.stream().map(Player::new).collect(Collectors.toSet()));
    }
}

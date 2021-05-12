package it.polimi.ingsw.server.events.send.graphics;

import it.polimi.ingsw.server.model.gameBoard.GameBoard;

import java.util.HashMap;
import java.util.Map;

public class FaithTracksUpdate extends GraphicsUpdateEvent{
    private final Map<String, Integer> faith = new HashMap<>();

    public FaithTracksUpdate() {
        super("faithTracks");
        GameBoard.getGameBoard().getFaithTracks().
                forEach(faithTrack -> faith.put(faithTrack.getOwner().getNickname(), faithTrack.getFaithMarker()));
    }
}

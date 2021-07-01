package it.polimi.ingsw.server.events.send.graphics;

import it.polimi.ingsw.server.model.gameBoard.GameBoard;

import java.util.HashMap;
import java.util.Map;

/**
 * Contains all the data to update the Faith Tracks of the Client.
 */
public class FaithTracksUpdate {

    private final Map<String, Integer> indexes = new HashMap<>();
    private final Map<String, Boolean[]> reports = new HashMap<>();

    /**
     * Create a new Faith Tracks Update by setting all the couples (Nickname, Last Position) and (Nickname, Activated Pope Tiles)
     */
    public FaithTracksUpdate() {
        GameBoard.getGameBoard().getFaithTracks().
                forEach(faithTrack -> {
                    indexes.put(faithTrack.getOwner().getNickname(), faithTrack.getFaithMarker());
                    reports.put(faithTrack.getOwner().getNickname(), faithTrack.getPopeReports());
                });
    }
}

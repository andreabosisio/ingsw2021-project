package it.polimi.ingsw.server.model.gameMode;

import it.polimi.ingsw.server.events.send.graphics.FaithTracksUpdate;
import it.polimi.ingsw.server.events.send.graphics.GraphicUpdateEvent;
import it.polimi.ingsw.server.model.gameBoard.GameBoard;
import it.polimi.ingsw.server.model.turn.TurnLogic;
import it.polimi.ingsw.server.utils.ServerParser;

/**
 * Class that represents the token that increments of two positions
 * the Black Cross Token in the Lorenzo's Faith Track
 */
public class DoubleFaithTrackProgressToken extends SoloActionToken {

    private final int FAITH_TRACK_PROGRESS = 2;

    public DoubleFaithTrackProgressToken() {
        super(ServerParser.DOUBLE_FAITH_TOKEN_TYPE);
    }

    /**
     * This method increments the position of the Black Cross Token of two positions.
     *
     * @param lorenzo   is used to access the Lorenzo's Faith Track
     * @param turnLogic is the TurnLogic reference
     * @return false
     */
    @Override
    public boolean doAction(Lorenzo lorenzo, TurnLogic turnLogic) {
        GameBoard.getGameBoard().faithProgress(lorenzo, FAITH_TRACK_PROGRESS);
        GraphicUpdateEvent graphicUpdateEvent = new GraphicUpdateEvent();
        graphicUpdateEvent.addUpdate(new FaithTracksUpdate());
        graphicUpdateEvent.addUpdate(lorenzo.getNickname() + " prayed hard so his Faith Marker is moving by " + FAITH_TRACK_PROGRESS + " positions");
        turnLogic.getModelInterface().notifyObservers(graphicUpdateEvent);
        return false;
    }

}
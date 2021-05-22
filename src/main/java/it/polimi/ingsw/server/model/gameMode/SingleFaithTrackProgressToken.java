package it.polimi.ingsw.server.model.gameMode;

import it.polimi.ingsw.server.events.send.graphics.FaithTracksUpdate;
import it.polimi.ingsw.server.events.send.graphics.GraphicUpdateEvent;
import it.polimi.ingsw.server.model.gameBoard.GameBoard;
import it.polimi.ingsw.server.model.turn.TurnLogic;

/**
 * Class that represents the token that increments of one position
 * the Black Cross Token in the Lorenzo's Faith Track
 */
public class SingleFaithTrackProgressToken implements SoloActionToken {

    /**
     * This method increments the position of the Black Cross Token of one position.
     * This is the only do Action method that return true,
     * in fact Lorenzo must shuffle the deck of Solo Action Tokens.
     *
     * @param lorenzo   is used to access the Lorenzo's Faith Track
     * @param turnLogic is the TurnLogic reference
     * @return true
     */
    @Override
    public boolean doAction(Lorenzo lorenzo, TurnLogic turnLogic) {
        int faithTrackProgress = 1;
        GameBoard.getGameBoard().faithProgress(lorenzo, faithTrackProgress);

        GraphicUpdateEvent graphicUpdateEvent = new GraphicUpdateEvent();
        graphicUpdateEvent.addUpdate(new FaithTracksUpdate());
        turnLogic.getModelInterface().notifyObservers(graphicUpdateEvent);
        return true;
    }
}
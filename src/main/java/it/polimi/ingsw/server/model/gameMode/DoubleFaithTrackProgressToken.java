package it.polimi.ingsw.server.model.gameMode;

import it.polimi.ingsw.server.model.gameBoard.GameBoard;

/**
 * Class that represents the token that increments of two positions
 * the Black Cross Token in the Lorenzo's Faith Track
 */
public class DoubleFaithTrackProgressToken implements SoloActionToken {

    /**
     * This method increments the position of the Black Cross Token of two positions.
     *
     * @param lorenzo   is used to access the Lorenzo's Faith Track
     * @return false
     */
    @Override
    public boolean doAction(Lorenzo lorenzo) {
        int faithTrackProgress = 2;
        GameBoard.getGameBoard().faithProgress(lorenzo, faithTrackProgress);
        return false;
    }

}
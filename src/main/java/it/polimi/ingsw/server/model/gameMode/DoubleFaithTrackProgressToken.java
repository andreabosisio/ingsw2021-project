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
     * @param gameBoard is used to increment the Lorenzo's Faith Track
     * @param lorenzo   is used to access the Lorenzo's Faith Track
     * @return false
     */
    @Override
    public boolean doAction(GameBoard gameBoard, Lorenzo lorenzo) {
        int faithTrackProgress = 2;
        gameBoard.faithProgress(lorenzo, faithTrackProgress);
        return false;
    }
}
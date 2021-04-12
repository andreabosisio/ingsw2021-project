package it.polimi.ingsw.server.model.gameMode;

import it.polimi.ingsw.server.model.gameBoard.GameBoard;

/**
 * Class that represents the token that increments of one position
 * the Black Cross Token in the Lorenzo's Faith Track
 */
public class SingleFaithTrackProgressToken implements SoloActionToken{

    /**
     * This method increments the position of the Black Cross Token of one position.
     * This is the only do Action method that return true,
     * in fact Lorenzo must shuffle the deck of Solo Action Tokens.
     * @param gameBoard is used to increment the Lorenzo's Faith Track
     * @param lorenzo is used to access the Lorenzo's Faith Track
     * @return true
     */
    @Override
    public boolean doAction(GameBoard gameBoard, Lorenzo lorenzo) {
        int faithTrackProgress = 1;
        gameBoard.faithProgress(lorenzo, faithTrackProgress);
        return true;
    }
}
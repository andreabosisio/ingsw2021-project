package it.polimi.ingsw.server.model.gameMode;

import it.polimi.ingsw.server.model.PlayerInterface;
import it.polimi.ingsw.server.model.gameBoard.EndGameObserver;

/**
 * Interface which allows to choose at runTime the correct methods that
 * decree the End Of The Game and select the Winner
 */
public interface ICheckWinner extends EndGameObserver {

    /**
     * Check if there was a condition of End of the Game.
     *
     * @return true if there was a condition of End of the Game
     */
    boolean isTheGameOver();

    /**
     * Method that shows the Winner of the Game.
     *
     * @return the Winner
     */
    PlayerInterface getWinner();
}
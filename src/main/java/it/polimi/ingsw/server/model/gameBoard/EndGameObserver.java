package it.polimi.ingsw.server.model.gameBoard;

import it.polimi.ingsw.server.model.PlayerInterface;

/**
 * Interface that contains all the methods useful for the correct
 * operating of the Pattern Observer.
 * This interface is implemented by the classes MultiPlayerCheckWinner
 * and SinglePlayerCheckWinner.
 */
public interface EndGameObserver {

    /**
     *
     * @param winner the winner in singleplayer
     */
    void update(PlayerInterface winner);

    /**
     *
     */
    void update();
}
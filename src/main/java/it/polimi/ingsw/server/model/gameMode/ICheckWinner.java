package it.polimi.ingsw.server.model.gameMode;

import it.polimi.ingsw.server.model.PlayerInterface;
import it.polimi.ingsw.server.model.gameBoard.EndGameObserver;

import java.util.List;

/**
 * Interface which allows to choose at runTime the correct methods that
 * decree the End Of The Game and select the Winner
 */
public interface ICheckWinner extends EndGameObserver {

    /**
     * Method that
     *
     * @return true if there was a condition of End of the Game
     */
    boolean isTheGameOver();

    /**
     * Method that decides the Winner of the Game.
     * @param players is the List of the Players who participated in the Game
     * @return the Winner
     */
    PlayerInterface getWinner(List<PlayerInterface> players);
}
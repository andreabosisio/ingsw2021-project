package it.polimi.ingsw.server.model.gameBoard;

import it.polimi.ingsw.server.model.PlayerInterface;

/**
 * Interface that contains all the methods useful for the correct
 * operating of the Pattern Observer.
 * This interface is extended by the class ICheckWinner.
 */
public interface EndGameObserver {

    /**
     * This method is called by the class FirstOfFaithTrack
     * when a player reaches the End of the Faith Track.
     * It's task is to set true the attribute gameOver.
     *
     * @param winner is the reference of the Player
     *               that reaches the last space of the Faith Track.
     */
    void update(PlayerInterface winner);

    /**
     * This method is called by the class PersonalBoard
     * and the class DevelopmentCardsGrid.
     * It's task is to set true the attribute gameOver.
     */
    void update();
}
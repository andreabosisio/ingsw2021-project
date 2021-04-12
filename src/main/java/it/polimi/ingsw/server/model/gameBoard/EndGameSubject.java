package it.polimi.ingsw.server.model.gameBoard;

/**
 * Interface that contains all the methods useful for the correct
 * operating of the Pattern Observer.
 */
public interface EndGameSubject {

    /**
     * This method is used to register an observer
     *
     * @param endGameObserver is the object to add.
     */
    void registerEndGameObserver(EndGameObserver endGameObserver);

    /**
     * This method calls the method update of the Observer.
     * Its task is to notify the class SinglePlayerCheckWinner or MultiPlayerCheckWinner
     * of the reach of a condition that causes the End Of The Game.
     */
    void notifyEndGameObserver();
}
package it.polimi.ingsw.server.model.gameBoard;

/**
 * Interface that contains all the methods useful for the correct
 * operating of the Pattern Observer.
 * This interface is implemented by the class FirstOfFaithTrack.
 */
public interface EndGameSubject {

    /**
     * This method is used by the GameBoard class to register the observer
     *
     * @param endGameObserver is the object to add.
     */
    void registerEndGameObserver(EndGameObserver endGameObserver);

    /**
     * This method calls the method update of the Observer.
     * Its task is to notify the class SinglePlayerCheckWinner or MultiPlayerCheckWinner
     * of the reach of the end of the Faith Track.
     */
    void notifyEndGameObservers();
}
package it.polimi.ingsw.server.model.gameBoard.faithtrack;

/**
 * Interface that contains all the methods useful for the correct
 * operating of the Pattern SendObserver.
 * This interface is implemented by the class FaithTracksManager.
 */
public interface FaithTracksManagerSubject {

    /**
     * This method is used by the GameBoard class to add the observers
     * in to the list faithTrackObservers.
     *
     * @param faithObserver is the object to add.
     */
    void registerFaithObserver(FaithTrack faithObserver);

    /**
     * This method calls the method update of all the Observers.
     * Its task is to notify all the Faith Tracks of the reach
     * of a one or more Pope space.
     * It also sets the next Pope space to check.
     *
     * @return true if a faithObserver flip a Pope Tile
     */
    boolean notifyFaithObservers();
}
package it.polimi.ingsw.server.model.gameBoard.faithtrack;

/**
 * Interface that contains all the methods useful for the correct
 * operating of the Pattern SendObserver.
 * This interface is implemented by the class FaithTrack.
 */
public interface FaithTracksManagerObserver {

    /**
     * This method checks if the current position of the Faith Marker
     * is on a space within (or beyond) the Vatican Report section specified,
     * in that case it makes true the correct attribute popeTile.
     *
     * @param indexOfTheVaticanReportSection is the index of the Vatican Report section
     * @return true if a popeTile is flipped up
     */
    boolean update(int indexOfTheVaticanReportSection);
}
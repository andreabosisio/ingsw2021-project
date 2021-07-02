package it.polimi.ingsw.server.model.gameBoard.faithtrack;

import it.polimi.ingsw.server.model.PlayerInterface;
import it.polimi.ingsw.server.model.gameBoard.EndGameObserver;
import it.polimi.ingsw.server.model.gameBoard.EndGameSubject;

import java.util.ArrayList;
import java.util.List;

/**
 * This class contains the information of the Faith Track with the
 * higher position of the Faith Marker.
 * It is a class observable, it has the task to notify the classes Faith Track and
 * the class MultiPlayerCheckWinner or SinglePlayerCheckWinner.
 */
public class FaithTracksManager implements FaithTracksManagerSubject, EndGameSubject {
    private final List<FaithTrack> faithTrackObservers;
    private EndGameObserver endGameObserver;
    private int currentPosition;
    private PlayerInterface currentFirstPlayer;
    private int nextPopeSpace;
    private final int FIRST_POPE_SPACE = 8;
    private final int SECOND_POPE_SPACE = 16;
    private final int THIRD_POPE_SPACE = 24;

    public FaithTracksManager() {
        this.faithTrackObservers = new ArrayList<>();
        this.currentPosition = 0;
        this.nextPopeSpace = FIRST_POPE_SPACE;
    }

    /**
     * This method is used by the GameBoard class to add the observers
     * in to the list faithTrackObservers.
     *
     * @param faithObserver is the object to add.
     */
    @Override
    public void registerFaithObserver(FaithTrack faithObserver) {
        faithTrackObservers.add(faithObserver);
    }

    /**
     * This method is used by the GameBoard class to add the observers
     * in to the list endGameObservers.
     *
     * @param endGameObserver is the object to add.
     */
    @Override
    public void registerEndGameObserver(EndGameObserver endGameObserver) {
        this.endGameObserver = endGameObserver;
    }

    /**
     * This method calls the method update of all the Observers.
     * Its task is to notify all the Faith Tracks of the reach
     * of a one or more Pope space.
     * It also sets the next Pope space to check.
     *
     * @return true if a faithObserver flip a Pope Tile
     */
    @Override
    public boolean notifyFaithObservers() {

        boolean checkFlip = false;

        if (checkIndexOfTheVaticanReportSection() != 0) {
            for (FaithTrack faithObserver : faithTrackObservers)
                if (faithObserver.update(checkIndexOfTheVaticanReportSection()))
                    checkFlip = true;
            if (checkIndexOfTheVaticanReportSection() == 1)
                nextPopeSpace = SECOND_POPE_SPACE;
            else if (checkIndexOfTheVaticanReportSection() == 2)
                nextPopeSpace = THIRD_POPE_SPACE;
            else if (checkIndexOfTheVaticanReportSection() == 3)
                nextPopeSpace = THIRD_POPE_SPACE + 1;
        }
        return checkFlip;
    }

    /**
     * This method is used by the method notifyObservers().
     * It controls if the class has notified for two times for the same tile,
     * in this case the method return 0.
     *
     * @return the index of the Vatican report section that the SendObserver must check.
     */
    private int checkIndexOfTheVaticanReportSection() {
        if (currentPosition >= nextPopeSpace) {
            if (nextPopeSpace == FIRST_POPE_SPACE)
                return 1;
            else if (nextPopeSpace == SECOND_POPE_SPACE)
                return 2;
            else if (nextPopeSpace == THIRD_POPE_SPACE)
                return 3;
        }
        return 0;
    }

    /**
     * This method calls the method update of the SendObserver.
     * Its task is to notify the class SinglePlayerCheckWinner or MultiPlayerCheckWinner
     * of the reach of the end of the Faith Track.
     */
    @Override
    public void notifyEndGameObserver() {
        this.endGameObserver.update(this.currentFirstPlayer);
    }

    /**
     * This method is called by the class Faith Track whenever it do an increment of its position
     * which involves the overcoming of the value contained in this.currentPosition.
     * In addition to update the attributes currentFirstPlayer and currentPosition,
     * the method calls the notifyWinnerObservers() if the observerCurrentPosition
     * is equal to the end of the Faith Track.
     *
     * @param observerPlayer          is the reference of the Player that calls the update
     * @param observerCurrentPosition is the position of the Faith Marker
     */
    public void updateFirstPosition(PlayerInterface observerPlayer, int observerCurrentPosition) {
        this.currentFirstPlayer = observerPlayer;
        this.currentPosition = observerCurrentPosition;
        if (observerCurrentPosition == THIRD_POPE_SPACE)
            this.notifyEndGameObserver();
    }

    /**
     * This method is used by the class Faith Track and
     *
     * @return the current position of the Faith Marker
     */
    public int getFirstPosition() {
        return this.currentPosition;
    }
}
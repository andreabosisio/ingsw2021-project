package it.polimi.ingsw.server.model.gameBoard.faithtrack;

import it.polimi.ingsw.server.model.PlayerInterface;

/**
 * This class contains all the information about
 * the faith track owned by a player.
 * It is observer of the class FirstOfFaithTrack.
 */
public class FaithTrack implements FaithObserver {
    PlayerInterface owner;
    int faithMarker;
    FirstOfFaithTrack firstOfFaithTrack;
    private final int endOfTheFaithTrack = 24;
    boolean popeTile1;
    boolean popeTile2;
    boolean popeTile3;

    public FaithTrack(PlayerInterface owner, FirstOfFaithTrack firstOfFaithTrack) {
        this.owner = owner;
        this.faithMarker = 0;
        this.firstOfFaithTrack = firstOfFaithTrack;
        this.popeTile1 = false;
        this.popeTile2 = false;
        this.popeTile3 = false;
    }

    /**
     * This method is called by the GameBoard to update
     * the position of the Faith Marker.
     *
     * @param progressValue is the increment value
     */
    public void faithTrackProgress(int progressValue) {
        faithMarker = faithMarker + progressValue;
        if (faithMarker > endOfTheFaithTrack)
            faithMarker = endOfTheFaithTrack;

        if (faithMarker > firstOfFaithTrack.getFirstPosition())
            firstOfFaithTrack.updateFirstPosition(this.owner, this.faithMarker);
    }

    /**
     * This method checks if the current position of the Faith Marker
     * is on a space within (or beyond) the Vatican Report section specified,
     * in that case it makes true the correct attribute popeTile.
     *
     * @param indexOfTheVaticanReportSection is the index of the Vatican Report section
     * @return true if a popeTile is flipped up
     */
    @Override
    public boolean update(int indexOfTheVaticanReportSection) {
        int startSection3 = 19;
        int startSection2 = 12;
        int startSection1 = 5;
        switch (indexOfTheVaticanReportSection) {
            case 1:
                if (faithMarker >= startSection1)
                    popeTile1 = true;
                else
                    return false;
                break;
            case 2:
                if (faithMarker >= startSection2)
                    popeTile2 = true;
                else
                    return false;
                break;
            case 3:
                if (faithMarker >= startSection3)
                    popeTile3 = true;
                else
                    return false;
                break;
        }
        return true;
    }

    /**
     * A get method that
     *
     * @return the owner of the Faith Track
     */
    public PlayerInterface getOwner() {
        return this.owner;
    }

    /**
     * Method that is called when the game is over.
     *
     * @return the points of The Faith Track
     */
    public int getVictoryPoints() {
        int victoryPoints = 0;

        if (faithMarker == endOfTheFaithTrack)
            victoryPoints = 20;
        else if (faithMarker >= 21)
            victoryPoints = 16;
        else if (faithMarker >= 18)
            victoryPoints = 12;
        else if (faithMarker >= 15)
            victoryPoints = 9;
        else if (faithMarker >= 12)
            victoryPoints = 6;
        else if (faithMarker >= 9)
            victoryPoints = 4;
        else if (faithMarker >= 6)
            victoryPoints = 2;
        else if (faithMarker >= 3)
            victoryPoints = 1;

        if (popeTile1)
            victoryPoints = victoryPoints + 2;
        if (popeTile2)
            victoryPoints = victoryPoints + 3;
        if (popeTile3)
            victoryPoints = victoryPoints + 4;

        return victoryPoints;
    }

    /**
     * Get methods, used for testing
     *
     * @return true if the Pope Tile is flipped up
     */
    public boolean isPopeTile1() {
        return popeTile1;
    }

    public boolean isPopeTile2() {
        return popeTile2;
    }

    public boolean isPopeTile3() {
        return popeTile3;
    }

    /**
     * Get method, used for testing
     *
     * @return the position of the Faith marker
     */
    public int getFaithMarker() {
        return faithMarker;
    }
}
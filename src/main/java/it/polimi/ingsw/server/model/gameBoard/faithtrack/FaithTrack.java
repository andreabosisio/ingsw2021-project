package it.polimi.ingsw.server.model.gameBoard.faithtrack;

import it.polimi.ingsw.server.model.PlayerInterface;

import java.util.Arrays;

/**
 * This class contains all the information about
 * the faith track owned by a player.
 * It is observer of the class FaithTracksManager.
 */
public class FaithTrack implements FaithTracksManagerObserver {
    public static final int DIM_POPE_REPORTS = 3;
    private static final int START_SECTION_3 = 19;
    private static final int START_SECTION_2 = 12;
    private static final int START_SECTION_1 = 5;
    private final int END_OF_FAITH_TRACK = 24;
    private final PlayerInterface owner;
    private int faithMarker;
    private final FaithTracksManager faithTracksManager;
    private final Boolean[] popeReports = new Boolean[DIM_POPE_REPORTS];

    /**
     * Create a new Faith Track by specifying the owner Player and the current First Player of the Faith Tracks.
     *
     * @param owner             Owner Player of the Faith Track
     * @param faithTracksManager First Player of the Faith Tracks
     */
    public FaithTrack(PlayerInterface owner, FaithTracksManager faithTracksManager) {
        this.owner = owner;
        this.faithMarker = 0;
        this.faithTracksManager = faithTracksManager;
        Arrays.fill(popeReports, Boolean.FALSE);
    }

    /**
     * This method is called by the GameBoard to update
     * the position of the Faith Marker.
     *
     * @param progressValue is the increment value
     */
    public void faithTrackProgress(int progressValue) {
        faithMarker = faithMarker + progressValue;
        if (faithMarker > END_OF_FAITH_TRACK)
            faithMarker = END_OF_FAITH_TRACK;

        if (faithMarker > faithTracksManager.getFirstPosition())
            faithTracksManager.updateFirstPosition(this.owner, this.faithMarker);
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
        switch (indexOfTheVaticanReportSection) {
            case 1:
                if (faithMarker >= START_SECTION_1)
                    popeReports[0] = true;
                else
                    return false;
                break;
            case 2:
                if (faithMarker >= START_SECTION_2)
                    popeReports[1] = true;
                else
                    return false;
                break;
            case 3:
                if (faithMarker >= START_SECTION_3)
                    popeReports[2] = true;
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

        if (faithMarker == END_OF_FAITH_TRACK)
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

        if (hasAchievedFirstReport())
            victoryPoints = victoryPoints + 2;
        if (hasAchievedSecondReport())
            victoryPoints = victoryPoints + 3;
        if (hasAchievedThirdReport())
            victoryPoints = victoryPoints + 4;

        return victoryPoints;
    }

    /**
     * Get method that return all the Pope Reports
     *
     * @return an Array of type Boolean with all the Pope Reports
     */
    public Boolean[] getPopeReports() {
        return popeReports;
    }

    /**
     * Return if the Player's achieved the first Pope Report.
     *
     * @return true if the Player's achieved the first Pope Report
     */
    public boolean hasAchievedFirstReport() {
        return popeReports[0];
    }

    /**
     * Return if the Player's achieved the second Pope Report.
     *
     * @return true if the Player's achieved the second Pope Report
     */
    public boolean hasAchievedSecondReport() {
        return popeReports[1];
    }

    /**
     * Return if the Player's achieved the second Pope Report.
     *
     * @return true if the Player's achieved the second Pope Report
     */
    public boolean hasAchievedThirdReport() {
        return popeReports[2];
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
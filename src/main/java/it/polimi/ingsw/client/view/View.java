package it.polimi.ingsw.client.view;

import it.polimi.ingsw.client.model.PersonalBoard;

import java.util.List;
import java.util.Map;

/**
 * This interface is implemented by the GUI and CLI Application
 */
public interface View {
    /**
     * Set the nickname of the player owner of this view
     *
     * @param nickname nickname of the player
     */
    void setNickname(String nickname);

    /**
     * Getter of nickname of the player using this view
     *
     * @return the player chosen nickname
     */
    String getNickname();

    /**
     * Set this client as currently playing
     *
     * @param isPlaying true if it is this client turn
     */
    void setIsPlaying(boolean isPlaying);

    /**
     * Method used to check if this client is playing
     *
     * @return true if he is
     */
    boolean isThisClientTurn();

    /**
     * Method used to show a waiting animation
     */
    void showWaitAnimation();

    /**
     * Method used to start the network with the server
     */
    void startNetwork();

    /**
     * Print an infoMessage from the server
     *
     * @param info message to print
     */
    void printInfoMessage(String info);

    /**
     * Print an errorMessage from the server
     *
     * @param error message to print
     */
    void printErrorMessage(String error);

    /**
     * Set the view in login phase
     */
    void setOnLogin();

    /**
     * Set the view in chose number of players phase
     *
     * @param payload message containing the max and min values permitted by the server
     */
    void setOnChooseNumberOfPlayers(String payload);

    /**
     * Set the view on the matchmaking phase
     */
    void setOnMatchMaking();

    /**
     * Set the view on the setup phase
     *
     * @param leaderCardsID    IDs of the leaders the player can chose from
     * @param numberOfResource number of resources the player needs to chose
     */
    void setOnSetup(List<String> leaderCardsID, int numberOfResource);

    /**
     * Set the player on the startTurn phase
     */
    void setOnYourTurn();

    /**
     * Set the player on the wait for other player turn to end
     *
     * @param currentPlayer player doing his turn
     */
    void setOnWaitForYourTurn(String currentPlayer);

    /**
     * Set the player on the placeDevelopmentCArd phase
     *
     * @param newCardID ID of the card to place
     */
    void setOnDevelopmentCardPlacement(String newCardID);

    /**
     * Set the player on the resource placement phase
     */
    void setOnResourcesPlacement();

    /**
     * Set the player on the resource transformation phase
     *
     * @param numberOfTransformation  number of resources to transform
     * @param possibleTransformations possible colors to transform them into
     */
    void setOnTransformation(int numberOfTransformation, List<String> possibleTransformations);

    /**
     * Set the player on the endTurn phase
     */
    void setOnEndTurn();

    /**
     * Show the player the endGame scene
     *
     * @param winner        winning player
     * @param playersPoints all players points
     */
    void setOnEndGame(String winner, Map<String, Integer> playersPoints);

    /**
     * Update the market state
     */
    void marketUpdate();

    /**
     * Update the development card grid state
     *
     * @param iD ID of the new card
     */
    void gridUpdate(String iD);

    /**
     * Update the faithTracks state
     */
    void faithTracksUpdate();

    /**
     * Updates the personalBoard of one player
     *
     * @param updatingPersonalBoard personalBoard to update
     */
    void personalBoardUpdate(PersonalBoard updatingPersonalBoard);
}

package it.polimi.ingsw.server.model.gameMode;

import it.polimi.ingsw.server.model.PlayerInterface;

import java.util.List;

/**
 * This class contains the methods to check the End of The Game and
 * to find the Winner, in fact it implements the interface ICheckWinner that extends the interface EndGameObserver.
 * It is Observer of the following classes:
 * DevelopmentCardsGrid, FirstOfFaithTrack and PersonalBoard.
 */
public class MultiPlayerCheckWinner implements ICheckWinner {
    private boolean gameOver = false;

    /**
     * This method is called by the class FirstOfFaithTrack
     * when a player reaches the End of the Faith Track.
     * Set the attribute gameOver true.
     *
     * @param winner is the reference of the Player
     *               that reaches the last space of the Faith Track.
     */
    @Override
    public void update(PlayerInterface winner) {
        gameOver = true;
    }

    /**
     * This method is called by the class PersonalBoard
     * when a Player buys his seventh Development Card.
     * Set the attribute gameOver true.
     */
    @Override
    public void update() {
        gameOver = true;
    }

    /**
     * Method that
     *
     * @return true if there was a condition of End of the Game
     */
    @Override
    public boolean isTheGameOver() {
        return gameOver;
    }

    /**
     * Method that collects the Victory Points of all the players
     * and declares the Winner.
     *
     * @param players is the List of the Players who participated in the Game
     * @return the Winner
     */
    @Override
    // TODO: Remember to test!
    public PlayerInterface getWinner(List<PlayerInterface> players) {
        int maxPoints = 0;
        PlayerInterface winner = null;

        for (PlayerInterface player : players) {
            if (player.getPersonalBoard().getPoints() > maxPoints) {
                maxPoints = player.getPersonalBoard().getPoints();
                winner = player;
            } else if (maxPoints != 0 && player.getPersonalBoard().getPoints() == maxPoints &&
                    player.getPersonalBoard().getResourcesLeft() > winner.getPersonalBoard().getResourcesLeft())
                winner = player;
        }
        return winner;
    }
}
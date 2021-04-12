package it.polimi.ingsw.server.model.gameMode;

import it.polimi.ingsw.server.model.PlayerInterface;

import java.util.List;

/**
 * This class contains the methods to check the End of The Game and
 * to find the Winner, in fact it implements the interface ICheckWinner that extends the interface EndGameObserver.
 * It is Observer of the following classes:
 * DevelopmentCardsGrid, FirstOfFaithTrack and PersonalBoard.
 */
public class SinglePlayerCheckWinner implements ICheckWinner {
    private PlayerInterface winner = null;
    private boolean gameOver = false;
    private final Lorenzo lorenzo;

    public SinglePlayerCheckWinner(Lorenzo lorenzo) {
        this.lorenzo = lorenzo;
    }

    /**
     * This method is called by the class FirstOfFaithTrack
     * when a player reaches the End of the Faith Track.
     * Set the attribute winner with the winnerGame and
     * set the attribute gameOver true
     *
     * @param winnerGame is the reference of the Player
     *                   that reaches the last space of the Faith Track.
     */
    @Override
    public void update(PlayerInterface winnerGame) {
        this.winner = winnerGame;
        gameOver = true;
    }

    /**
     * This method is called by the class DevelopmentCardsGrid
     * when all the Development Cards of a certain color are no longer available
     * (the cards has been bought or discarded).
     * Set the attribute winner with Lorenzo and set the attribute gameOver true.
     */
    @Override
    public void update() {
        winner = lorenzo;
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
     * Method that decides the Winner of the Game.
     *
     * @param players is the List that contains the unique Player
     *                that participates in the Game
     * @return the Winner: the Player or Lorenzo
     */
    @Override
    public PlayerInterface getWinner(List<PlayerInterface> players) {
        return winner;
    }
}
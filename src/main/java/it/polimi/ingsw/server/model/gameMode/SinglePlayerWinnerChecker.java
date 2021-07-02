package it.polimi.ingsw.server.model.gameMode;

import it.polimi.ingsw.server.model.PlayerInterface;

/**
 * This class contains the methods to check the End of The Game and
 * to find the Winner, in fact it implements the interface ICheckWinner that extends the interface EndGameObserver.
 * It is SendObserver of the following classes:
 * DevelopmentCardsGrid, FaithTracksManager and PersonalBoard.
 */
public class SinglePlayerWinnerChecker implements ICheckWinner {
    private PlayerInterface winner = null;
    private boolean gameOver = false;
    private final Lorenzo lorenzo;
    private final PlayerInterface player;

    public SinglePlayerWinnerChecker(Lorenzo lorenzo, PlayerInterface player) {
        this.lorenzo = lorenzo;
        this.player = player;
    }

    /**
     * This method is called by the class FaithTracksManager
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
     * (the cards has been bought or discarded) and it is called by the class PersonalBoard
     * Set the attribute winner with Lorenzo and set the attribute gameOver true.
     *
     * @param lorenzoWin is set true if Lorenzo is the winner, false otherwise
     */
    @Override
    public void update(boolean lorenzoWin) {
        if (lorenzoWin)
            winner = lorenzo;
        else
            winner = player;
        gameOver = true;
    }

    /**
     * Check if there was a condition of End of the Game.
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
     * @return the Winner: the Player or Lorenzo
     */
    @Override
    public PlayerInterface getWinner() {
        return winner;
    }
}
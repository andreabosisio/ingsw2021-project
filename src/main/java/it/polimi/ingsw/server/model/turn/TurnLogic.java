package it.polimi.ingsw.server.model.turn;

import it.polimi.ingsw.server.model.gameBoard.GameBoard;
import it.polimi.ingsw.server.model.gameMode.GameMode;
import it.polimi.ingsw.server.model.player.Player;

import java.util.List;

/**
 *
 */
public class TurnLogic {
    private final GameMode gameMode;
    private final List<Player> players;
    private Player currentPlayer;
    private State currentState;
    private GameBoard gameBoard;

    public TurnLogic(List<Player> players) {
        this.players = players;
        this.currentPlayer = players.get(0);
        this.currentState = new StartTurn();
        GameBoard.getGameBoard().createFaithTracks(players);
        GameBoard.getGameBoard().setMarketTray(this);
        this.gameBoard = GameBoard.getGameBoard();
        gameMode = new GameMode(players);
        this.setTheObservers();
    }

    /**
     * This method set all the observers for the correct functional of the two Observer Pattern.
     * One is used for the Faith Tracks to check the reach of a PopeSpace.
     * The other one is used by the classes that implement the interface ICheckWinner
     * to check all the condition of End of Game and to decree the Winner.
     */
    private void setTheObservers() {
        // Set the Observer of the Personal Boards
        for (Player player : players)
            player.getPersonalBoard().registerEndGameObserver(gameMode.getICheckWinner());
        // Set the Observer of the First Of Faith Track
        GameBoard.getGameBoard().setObserversOfFirstOfFaithTrack(GameBoard.getGameBoard().getFaithTracks(), gameMode.getICheckWinner());
        // Set the Observer of the Development Cards Grid
        GameBoard.getGameBoard().setObserverOfDevCardsGrid(gameMode.getICheckWinner());
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

}
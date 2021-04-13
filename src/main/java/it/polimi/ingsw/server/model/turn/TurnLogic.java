package it.polimi.ingsw.server.model.turn;

import it.polimi.ingsw.server.model.gameBoard.GameBoard;
import it.polimi.ingsw.server.model.gameMode.GameMode;
import it.polimi.ingsw.server.model.player.Player;

import java.util.List;

public class TurnLogic {
    private final GameMode gameMode;
    private final List<Player> players;
    private Player currentPlayer;
    private State currentState;

    public TurnLogic(List<Player> players) {
        this.players = players;
        this.currentPlayer = players.get(0);
        this.currentState = new StartTurn();
        gameMode = new GameMode(players);
        this.setObserverOfThePersonalBoards();
        GameBoard.getGameBoard().createFaithTracks(players);
        GameBoard.getGameBoard().setObserversOfFirstOfFaithTrack(GameBoard.getGameBoard().getFaithTracks(), gameMode.getICheckWinner());
        GameBoard.getGameBoard().setObserverOfDevCardsGrid(gameMode.getICheckWinner());
    }

    private void setObserverOfThePersonalBoards() {
        for (Player player : players)
            player.getPersonalBoard().registerEndGameObserver(gameMode.getICheckWinner());
    }

    public GameMode getGameMode() {
        return gameMode;
    }
}

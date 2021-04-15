package it.polimi.ingsw.server.model.turn;

import it.polimi.ingsw.server.model.gameBoard.GameBoard;
import it.polimi.ingsw.server.model.gameMode.GameMode;
import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.server.model.resources.Resource;
import it.polimi.ingsw.server.model.resources.WhiteResource;

import java.util.List;

/**
 *
 */
public class TurnLogic {
    private final GameMode gameMode;
    private final List<Player> players;
    private Player currentPlayer;
    private State currentState;
    private final State startTurn,leaderState,waitDevCardPlacement,waitTransformation,waitResourcePlacement,endTurn,lorenzoTurn;
    private List<Resource> whiteResourcesFromMarket;

    public TurnLogic(List<Player> players) {
        this.players = players;
        this.currentPlayer = players.get(0);
        this.currentState = new StartTurn(this);
        GameBoard.getGameBoard().createFaithTracks(players);
        GameBoard.getGameBoard().setTurnLogicOfMarketTray(this);
        this.gameMode = new GameMode(players);
        this.setTheObservers();

        this.startTurn = new StartTurn(this);
        this.leaderState = new LeaderState(this);
        this.waitDevCardPlacement = new WaitDevCardPlacement(this);
        this.waitTransformation = new WaitTransformation(this);
        this.waitResourcePlacement = new WaitResourcePlacement(this);
        this.endTurn = new EndTurn(this);
        this.lorenzoTurn = new LorenzoTurn(this);

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

    public void setCurrentState(State currentState) {
        this.currentState = currentState;
    }

    public State getStartTurn() {
        return startTurn;
    }

    public State getLeaderState() {
        return leaderState;
    }

    public State getWaitDevCardPlacement() {
        return waitDevCardPlacement;
    }

    public State getWaitTransformation() {
        return waitTransformation;
    }

    public State getWaitResourcePlacement() {
        return waitResourcePlacement;
    }

    public State getEndTurn() {
        return endTurn;
    }

    public State getLorenzoTurn() {
        return lorenzoTurn;
    }

    public void setWhiteResourcesFromMarket(WhiteResource whiteResourceFromMarket) {
        this.whiteResourcesFromMarket.add(whiteResourceFromMarket);
    }

    public List<Resource> getWhiteResourcesFromMarket() {
        return whiteResourcesFromMarket;
    }
}
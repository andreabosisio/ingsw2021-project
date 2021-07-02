package it.polimi.ingsw.server.model.gameBoard;

import it.polimi.ingsw.server.model.PlayerInterface;
import it.polimi.ingsw.server.model.cards.LeaderCard;
import it.polimi.ingsw.server.model.gameBoard.faithtrack.FaithTrack;
import it.polimi.ingsw.server.model.gameBoard.faithtrack.FaithTracksManager;
import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.server.model.turn.TurnLogic;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the board used by the players to play the Game.
 * It contains a deck of the Leader cards, a Development cards grid, a market tray and
 * a list of all the Fait Track owned by the players.
 */
public class GameBoard {
    private DeckLeader deckLeader;
    private MarketTray marketTray;
    private DevelopmentCardsGrid developmentCardsGrid;
    private List<FaithTrack> faithObservers;
    private FaithTracksManager faithTracksManager;
    private static GameBoard instance = null;

    /**
     * reset for testing being a singleton class
     */
    public void reset() {
        this.deckLeader = new DeckLeader();
        this.marketTray = new MarketTray();
        this.developmentCardsGrid = new DevelopmentCardsGrid();
        this.faithObservers = new ArrayList<>();
        this.faithTracksManager = new FaithTracksManager();
    }

    /**
     * Create an instance of GameBoard or return the existing one
     *
     * @return the only existing instance of GameBoard
     */
    public static synchronized GameBoard getGameBoard() {
        if (instance == null) {
            instance = new GameBoard();
        }
        return instance;
    }

    /**
     * Create the only existing GameBoard by calling all the needed constructors.
     */
    private GameBoard() {
        this.deckLeader = new DeckLeader();
        this.marketTray = new MarketTray();
        this.developmentCardsGrid = new DevelopmentCardsGrid();
        this.faithObservers = new ArrayList<>();
        this.faithTracksManager = new FaithTracksManager();
    }

    /**
     * Getter method that return the Market Tray.
     *
     * @return the Market Tray
     */
    public MarketTray getMarketTray() {
        return marketTray;
    }

    /**
     * Get method that return the Development Cards Grid.
     *
     * @return the Development Cards Grid
     */
    public DevelopmentCardsGrid getDevelopmentCardsGrid() {
        return developmentCardsGrid;
    }

    /**
     * Set method that creates the list of the Faith Tracks owned by the Players if it's not a SinglePlayer game.
     *
     * @param players is the list of Players that are in the Game
     * @return true if it's not a SinglePlayer game, false otherwise
     */
    public boolean createFaithTracks(List<Player> players) {
        if (players.size() > 0) {
            for (Player player : players) {
                FaithTrack playerFaithTrack = new FaithTrack(player, faithTracksManager);
                faithObservers.add(playerFaithTrack);
            }
            return true;
        }
        return false;
    }

    /**
     * This method is used by the constructor to set the list of the FaithObserver and
     * the unique EndGameObserver of the class FaithTracksManager
     *
     * @param faithObservers is the List of Faith Track that are SendObserver of the class FaithTracksManager
     * @param iCheckWinner   is the observer of the class FaithTracksManager
     */
    public void setObserversOfFirstOfFaithTrack(List<FaithTrack> faithObservers, EndGameObserver iCheckWinner) {
        for (FaithTrack faithObserver : faithObservers) {
            faithTracksManager.registerFaithObserver(faithObserver);
        }
        faithTracksManager.registerEndGameObserver(iCheckWinner);
    }

    /**
     * This method is used by the constructor to set the observer
     * of the class DevelopmentCardsGrid
     *
     * @param iCheckWinner is the observer of the class FaithTracksManager
     */
    public void setObserverOfDevCardsGrid(EndGameObserver iCheckWinner) {
        developmentCardsGrid.registerEndGameObserver(iCheckWinner);
    }

    /**
     * This method is used by the class GameMode to create the Lorenzo's Faith Track
     * and to add to the list faithObservers
     *
     * @param lorenzo Lorenzo
     */
    public void createLorenzoFaithTrack(PlayerInterface lorenzo) {
        faithObservers.add(new FaithTrack(lorenzo, this.faithTracksManager));
    }

    /**
     * This method increases the Faith Track Marker of the Faith Track owned by a player
     * and calls the method notifyObservers() of the FaithTracksManager class.
     *
     * @param player        is the owner of the Faith Track
     * @param progressValue is the increment value
     * @return true if a tile is flipped up
     */
    public boolean faithProgress(PlayerInterface player, int progressValue) {
        faithObservers.stream().filter(x -> x.getOwner() == player).forEach(x -> x.faithTrackProgress(progressValue));
        return faithTracksManager.notifyFaithObservers();
    }

    /**
     * This method increases the Faith Track Marker of all the Faith Track
     * except that belongs to player
     * and calls the method notifyObservers() of the FaithTracksManager class.
     *
     * @param currentPlayer is the Player who has not an increase of the Faith Track
     * @param progressValue is the increment value
     * @return true if a tile is flipped up
     */
    public boolean faithProgressForOtherPlayers(PlayerInterface currentPlayer, int progressValue) {
        faithObservers.stream().filter(x -> x.getOwner() != currentPlayer).forEach(x -> x.faithTrackProgress(progressValue));
        return faithTracksManager.notifyFaithObservers();
    }

    /**
     * Get method that return the first hand of the Leader Card
     *
     * @return the first four Leader Card
     */
    public List<LeaderCard> draw4LeaderCards() {
        return deckLeader.draw4();
    }

    /**
     * Get method that
     *
     * @return the deck of the Leader Cards
     */
    public DeckLeader getDeckLeader() {
        return deckLeader;
    }

    /**
     * Get method that
     *
     * @return the list of all the Faith Tracks
     */
    public List<FaithTrack> getFaithTracks() {
        return this.faithObservers;
    }

    /**
     * Method that return the Faith Track of the Player
     *
     * @param player in input
     * @return the Faith Track owned by player
     */
    public FaithTrack getFaithTrackOfPlayer(PlayerInterface player) {
        return faithObservers.stream().filter(p -> p.getOwner().equals(player)).findFirst().orElse(null);
    }

    /**
     * This method is used to save in the marketTray the turnLogic it will operate on
     * This will be used when a player with 2 transformation leaders will take white resources
     *
     * @param turnLogic turnLogic of the game
     */
    public void setTurnLogicOfMarketTray(TurnLogic turnLogic) {
        this.marketTray.setTurn(turnLogic);
    }
}
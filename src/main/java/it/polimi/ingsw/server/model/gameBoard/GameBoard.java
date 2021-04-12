package it.polimi.ingsw.server.model.gameBoard;

import it.polimi.ingsw.server.model.PlayerInterface;
import it.polimi.ingsw.server.model.cards.LeaderCard;
import it.polimi.ingsw.server.model.gameBoard.faithtrack.FaithTrack;
import it.polimi.ingsw.server.model.gameBoard.faithtrack.FirstOfFaithTrack;
import it.polimi.ingsw.server.model.player.Player;

import java.util.*;

/**
 * This class represents the board used by the players to play the Game.
 * It contains a deck of the Leader cards, a Development cards grid, a market tray and
 * a list of all the Fait Track owned by the players.
 */
public class GameBoard {
    private final DeckLeader deckLeader;
    private final MarketTray marketTray;
    private final List<FaithTrack> faithObservers;
    private final FirstOfFaithTrack firstOfFaithTrack;

    public GameBoard(List<Player> players, EndGameObserver iCheckWinner) {
        this.deckLeader = new DeckLeader();
        this.marketTray = new MarketTray();
        this.faithObservers = new ArrayList<>();
        this.firstOfFaithTrack = new FirstOfFaithTrack();
        for (Player player : players) {
            FaithTrack playerFaithTrack = new FaithTrack(player, firstOfFaithTrack);
            faithObservers.add(playerFaithTrack);
            player.getPersonalBoard().setFaithTrack(playerFaithTrack);
        }
        this.setObserversOfFirstOfFaithTrack(faithObservers, iCheckWinner);
        this.setObserverOfDevCardsGrid(iCheckWinner);
    }

    /**
     * This method is used by the constructor to set the list of the FaithObserver and
     * the unique EndGameObserver of the class FirstOfFaithTrack
     *
     * @param faithObservers is the List of Faith Track that are Observer of the class FirstOfFaithTrack
     * @param iCheckWinner   is the observer of the class FirstOfFaithTrack
     */
    private void setObserversOfFirstOfFaithTrack(List<FaithTrack> faithObservers, EndGameObserver iCheckWinner) {
        for (FaithTrack faithObserver : faithObservers) {
            firstOfFaithTrack.registerFaithObserver(faithObserver);
        }
        firstOfFaithTrack.registerEndGameObserver(iCheckWinner);
    }

    /**
     * This method is used by the constructor to set the observer
     * of the class DevelopmentCardsGrid
     *
     * @param iCheckWinner is the observer of the class FirstOfFaithTrack
     */
    private void setObserverOfDevCardsGrid(EndGameObserver iCheckWinner) {
        DevelopmentCardsGrid.getDevelopmentCardsGrid().registerEndGameObserver(iCheckWinner);
    }

    /**
     * This method increases the Faith Track Marker of the Faith Track owned by a player
     * and calls the method notifyObservers() of the FirstOfFaithTrack class.
     *
     * @param player        is the owner of the Faith Track
     * @param progressValue is the increment value
     * @return true if a tile is flipped up
     */
    public boolean faithProgress(PlayerInterface player, int progressValue) {
        faithObservers.stream().filter(x -> x.getOwner() == player).forEach(x -> x.faithTrackProgress(progressValue));
        return firstOfFaithTrack.notifyFaithObservers();
    }

    /**
     * This method increases the Faith Track Marker of all the Faith Track
     * except that belongs to player
     * and calls the method notifyObservers() of the FirstOfFaithTrack class.
     *
     * @param player        is the Player who has not an increase of the Faith Track
     * @param progressValue is the increment value
     * @return true if a tile is flipped up
     */
    public boolean faithProgressOfRestOfPlayers(PlayerInterface player, int progressValue) {
        faithObservers.stream().filter(x -> x.getOwner() != player).forEach(x -> x.faithTrackProgress(progressValue));
        return firstOfFaithTrack.notifyFaithObservers();
    }

    /**
     * Get method that return the first hand of the Leader Card
     *
     * @return the first four Leader Card
     */
    public List<LeaderCard> drawLeader() {
        return deckLeader.draw();
    }


    /**
     * Method used for testing
     *
     * @return the reference of the FirstOfFaithTrack
     */
    public FirstOfFaithTrack getFirstOfFaithTrack() {
        return firstOfFaithTrack;
    }

    /**
     * Method used for testing
     *
     * @param player in input
     * @return the Faith Track owned by player
     */
    public FaithTrack getFaithTrackPlayer(PlayerInterface player) {
        return faithObservers.stream().filter(p -> p.getOwner().equals(player)).findFirst().orElse(null);
    }
}
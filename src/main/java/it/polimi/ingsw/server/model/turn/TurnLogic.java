package it.polimi.ingsw.server.model.turn;

import it.polimi.ingsw.server.events.send.EventToClient;
import it.polimi.ingsw.server.events.send.ReconnectEvent;
import it.polimi.ingsw.server.events.send.StartTurnEvent;
import it.polimi.ingsw.server.events.send.graphics.*;
import it.polimi.ingsw.server.model.ModelInterface;
import it.polimi.ingsw.server.model.cards.DevelopmentCard;
import it.polimi.ingsw.server.model.gameBoard.GameBoard;
import it.polimi.ingsw.server.model.gameMode.GameMode;
import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.server.model.resources.WhiteResource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Contains all the information of the current turn
 */
public class TurnLogic {
    private EventToClient lastEventSent;

    private final GameMode gameMode;

    private final List<Player> players;
    private Player currentPlayer;

    private List<WhiteResource> whiteResourcesFromMarket = new ArrayList<>();
    private DevelopmentCard chosenDevCard;

    private final ModelInterface modelInterface;

    /**
     * Used to construct a new turnLogic and everything needed for the model to work
     *
     * @param players        players in the game
     * @param modelInterface modelInterface accessible from the controller
     */
    public TurnLogic(List<Player> players, ModelInterface modelInterface) {
        this.lastEventSent = null;
        this.players = players;
        this.modelInterface = modelInterface;
        this.currentPlayer = players.get(players.size() - 1);
        GameBoard.getGameBoard().createFaithTracks(players);
        GameBoard.getGameBoard().setTurnLogicOfMarketTray(this);
        this.gameMode = new GameMode(players);
        this.setTheObservers();
    }

    /**
     * This method set all the observers for the correct functional of the two SendObserver Pattern.
     * One is used for the Faith Tracks to check the reach of a PopeSpace.
     * The other one is used by the classes that implement the interface ICheckWinner
     * to check all the condition of End of Game and to decree the Winner.
     */
    private void setTheObservers() {
        // Set the SendObserver of the Personal Boards
        for (Player player : players)
            player.getPersonalBoard().registerEndGameObserver(gameMode.getICheckWinner());
        // Set the SendObserver of the First Of Faith Track
        GameBoard.getGameBoard().setObserversOfFirstOfFaithTrack(GameBoard.getGameBoard().getFaithTracks(), gameMode.getICheckWinner());
        // Set the SendObserver of the Development Cards Grid
        GameBoard.getGameBoard().setObserverOfDevCardsGrid(gameMode.getICheckWinner());
    }

    /**
     * Return true if it's the turn of the last player.
     *
     * @return true if it's the turn of the last player
     */
    public boolean isLastPlayerTurn() {
        List<Player> onlinePlayers = players.stream().filter(p -> (p.isOnline() || currentPlayer.equals(p))).collect(Collectors.toList());
        return onlinePlayers.indexOf(currentPlayer) == onlinePlayers.size() - 1;
    }

    /**
     * Set the next player and reset the current values.
     */
    public void setNextPlayer() {

        if (isLastPlayerTurn())
            currentPlayer = players.get(0);
        else
            currentPlayer = players.get(players.indexOf(currentPlayer) + 1);

        if (!currentPlayer.isOnline()) {
            setNextPlayer();
            return;
        }

        modelInterface.setCurrentState(modelInterface.getStartTurn());
        //if player has no saved state from disconnection
        if (!currentPlayer.prepareTurn(modelInterface)) {
            modelInterface.notifyObservers(new StartTurnEvent(currentPlayer.getNickname()));
            setLastEventSent(new StartTurnEvent(currentPlayer.getNickname(), currentPlayer.getNickname()));
            reset();
        } else {
            modelInterface.notifyObservers(new StartTurnEvent(currentPlayer.getNickname(), players.stream().map(Player::getNickname).filter(n -> !n.equals(currentPlayer.getNickname())).toArray(String[]::new)));
        }
    }

    /**
     * This method is used to reset the turnLogic for a new player' turn
     */
    private void reset() {
        whiteResourcesFromMarket.clear();
        chosenDevCard = null;
    }

    /**
     * This method is used to get the gameMode the model is operating on
     *
     * @return the model' gameMode
     */
    public GameMode getGameMode() {
        return gameMode;
    }

    /**
     * This method is used to return the player currently doing his turn
     *
     * @return the currently playing player
     */
    public Player getCurrentPlayer() {
        return currentPlayer;
    }


    /**
     * This method is used to add a single white resource to the list of  white resources the player must transform
     *
     * @param whiteResourceFromMarket white resource to add
     */
    public void addWhiteResourcesFromMarketToTransform(WhiteResource whiteResourceFromMarket) {
        this.whiteResourcesFromMarket.add(whiteResourceFromMarket);
    }

    /**
     * This method is used to set a list of white resource as the list of  white resources the player must transform
     *
     * @param whiteResourcesFromMarket resources to set as the white resources to transform
     */
    public void addWhiteResourcesFromMarketToTransform(List<WhiteResource> whiteResourcesFromMarket) {
        this.whiteResourcesFromMarket = new ArrayList<>(whiteResourcesFromMarket);
    }

    /**
     * This method is esed to get the list of white resources the player must transform this turn
     *
     * @return the list of white resources to transform
     */
    public List<WhiteResource> getWhiteResourcesFromMarket() {
        return whiteResourcesFromMarket;
    }

    /**
     * This method is used to get the modelInterface visible to the controller
     *
     * @return the visible modelInterface
     */
    public ModelInterface getModelInterface() {
        return modelInterface;
    }

    /**
     * This method is used to set the development card the player must position
     *
     * @param chosenDevCard development card the player must position
     */
    public void setChosenDevCard(DevelopmentCard chosenDevCard) {
        this.chosenDevCard = chosenDevCard;
    }

    /**
     * This method is used to get the development card the player must place this turn
     *
     * @return development card the player must position
     */
    public DevelopmentCard getChosenDevCard() {
        return chosenDevCard;
    }

    /**
     * getter for all players in the game
     *
     * @return list of players in the game
     */
    public List<Player> getPlayers() {
        return players;
    }


    /**
     * Saves the last event sent to the observers
     *
     * @param lastEventSent event to save
     */
    public void setLastEventSent(EventToClient lastEventSent) {
        this.lastEventSent = lastEventSent;
    }

    /**
     * Resend the last event sent to the observers
     * It is used in case of a failed action
     */
    public void reSendLastEvent() {
        if (lastEventSent != null) {
            modelInterface.notifyObservers(lastEventSent);
        }
    }

    /**
     * This method is used to disconnect a player during the game
     * If it was this player turn his turnState is saved for a later reconnection and the next player in line starts his turn
     *
     * @param nickname nickname of the player to disconnect
     * @return if the player was the last one online
     */
    protected boolean disconnectPlayer(String nickname) {
        Player disconnected = modelInterface.getPlayerByNickname(nickname);
        assert disconnected != null;
        disconnected.setOnline(false);
        if (players.stream().noneMatch(Player::isOnline))
            return true;
        if (currentPlayer.equals(disconnected)) {
            currentPlayer.setDisconnectedData(modelInterface.getCurrentState(), whiteResourcesFromMarket, chosenDevCard, lastEventSent);
            setNextPlayer();
            modelInterface.setCurrentState(modelInterface.getStartTurn());
            setLastEventSent(new StartTurnEvent(currentPlayer.getNickname(), currentPlayer.getNickname()));
        }
        return false;
    }

    /**
     * This method is used to reconnect a player during the game
     *
     * @param nickname nickname of the player to reconnect
     */
    protected void reconnectPlayer(String nickname) {
        GraphicUpdateEvent graphicsForReconnection = new GraphicUpdateEvent();
        graphicsForReconnection.addUpdate(new MarketUpdate());
        graphicsForReconnection.addUpdate(new GridUpdate());
        graphicsForReconnection.addUpdate(new FaithTracksUpdate());
        players.forEach(player -> graphicsForReconnection.addUpdate(new PersonalBoardUpdate(player, new FullProductionSlotsUpdate(), new LeaderCardSlotsUpdate(), new WarehouseUpdate())));
        modelInterface.notifyObservers(new ReconnectEvent(nickname, currentPlayer.getNickname(), players.stream().map(Player::getNickname).collect(Collectors.toList()), graphicsForReconnection));
        Player reconnected = modelInterface.getPlayerByNickname(nickname);
        assert reconnected != null;
        reconnected.setOnline(true);
    }


    /**
     * Return the last event sent to the currentPlayer
     *
     * @return the last EventToClient
     */
    protected EventToClient getLastEventSent() {
        return lastEventSent;
    }
}

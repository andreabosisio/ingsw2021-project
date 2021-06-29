package it.polimi.ingsw.server.model.turn;

import it.polimi.ingsw.server.exceptions.*;
import it.polimi.ingsw.server.events.send.ReconnectEvent;
import it.polimi.ingsw.server.events.send.SendEvent;
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
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Contains all the information of the current turn
 */
public class TurnLogic {
    private SendEvent lastEventSent;

    private final GameMode gameMode;
    private final List<Player> players;
    private Player currentPlayer;
    private State currentState;
    private final State startTurn, waitDevCardPlacement, waitTransformation, waitResourcePlacement, endTurn, endGame, idle;

    private List<WhiteResource> whiteResourcesFromMarket = new ArrayList<>();
    private DevelopmentCard chosenDevCard;

    private final ModelInterface modelInterface;

    /**
     * Used to construct a new turnLogic and everything needed for the model to work
     *
     * @param players players in the game
     * @param modelInterface modelInterface accessible from the controller
     */
    public TurnLogic(List<Player> players, ModelInterface modelInterface) {
        this.lastEventSent = null;
        this.modelInterface = modelInterface;
        this.players = players;
        this.currentPlayer = players.get(players.size()-1);
        GameBoard.getGameBoard().createFaithTracks(players);
        GameBoard.getGameBoard().setTurnLogicOfMarketTray(this);
        this.gameMode = new GameMode(players);
        this.setTheObservers();
        this.startTurn = new StartTurnState(this);
        this.waitDevCardPlacement = new WaitDevelopmentCardPlacementState(this);
        this.waitTransformation = new WaitTransformationState(this);
        this.waitResourcePlacement = new WaitResourcePlacementState(this);
        this.endTurn = new EndTurnState(this);
        this.endGame = new EndGameState(this);
        this.idle = new IdleState(this);
        this.currentState = getIdle();
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
        //todo bug if last player disconnect!!!!!!
        //because disconnected player is offline
        List<Player> onlinePlayers = players.stream().filter(p->(p.isOnline() || currentPlayer.equals(p))).collect(Collectors.toList());
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
        setCurrentState(getStartTurn());
        if (!currentPlayer.prepareTurn(this)) {
            modelInterface.notifyObservers(new StartTurnEvent(currentPlayer.getNickname()));
            setLastEventSent(new StartTurnEvent(getCurrentPlayer().getNickname(), getCurrentPlayer().getNickname()));
            reset();
        }
        else{
            modelInterface.notifyObservers(new StartTurnEvent(currentPlayer.getNickname(),players.stream().map(Player::getNickname).filter(n->!n.equals(currentPlayer.getNickname())).toArray(String[]::new)));
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
     * This method is used to set the current turnLogic state
     *
     * @param currentState state to set as current
     */
    public void setCurrentState(State currentState) {
        this.currentState = currentState;
    }

    /**
     * This method is used to get the startTurn state of the turnLogic
     *
     * @return the turnLogic' startTurn state
     */
    public State getStartTurn() {
        return startTurn;
    }

    /**
     * This method is used to get the waitDevCardPlacement state of the turnLogic
     *
     * @return the turnLogic' waitDevCardPlacement state
     */
    public State getWaitDevCardPlacement() {
        return waitDevCardPlacement;
    }

    /**
     * This method is used to get the waitTransformation state of the turnLogic
     *
     * @return the turnLogic' waitTransformation state
     */
    public State getWaitTransformation() {
        return waitTransformation;
    }

    /**
     * This method is used to get the waitResourcePlacement state of the turnLogic
     *
     * @return the turnLogic' waitResourcePlacement state
     */
    public State getWaitResourcePlacement() {
        return waitResourcePlacement;
    }

    /**
     * This method is used to get the endTurn state of the turnLogic
     *
     * @return the turnLogic' endTurn state
     */
    public State getEndTurn() {
        return endTurn;
    }

    /**
     * This method is used to get the endGame state of the turnLogic
     *
     * @return the turnLogic' endGame state
     */
    public State getEndGame() {
        return endGame;
    }

    /**
     * This method is used to get the idle state of the turnLogic
     *
     * @return the turnLogic' idle state
     */
    public State getIdle() {
        return idle;
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
     * Take the chosen resources from the MarketTray and set the current state of the game to
     * WaitResourceTransformation if there are some White Resources to transform or else to
     * WaitResourcePlacementState.
     *
     * @param arrowID is the index of the chosen line of the MarketTray
     * @return true if the state has been changed
     * @throws InvalidIndexException if the arrowID is not correct
     * @throws InvalidEventException if the market action failed
     */
    public boolean marketAction(int arrowID) throws InvalidEventException, InvalidIndexException {
        return currentState.marketAction(arrowID);
    }

    /**
     * For all the given ProductionCard apply the production with the chosen resources.
     *
     * @param inResourcesForEachProductions  containing the chosen ProductionCard and the chosen resources to apply its production
     * @param outResourcesForEachProductions containing the chosen ProductionCard and (if possible) the desired resources
     * @return true if the production has been correctly applied
     * @throws InvalidEventException        if one of the production can't be applied
     * @throws InvalidIndexException        if one of the index of the chosen ProductionCard doesn't exists
     * @throws NonStorableResourceException if one of the chosen resources contains a NonStorableResource
     * @throws EmptySlotException           if the first position of any swap pair doesn't contain a resource
     * @throws NonAccessibleSlotException   if the slot the player chose couldn't be accessed
     */
    public boolean productionAction(Map<Integer, List<Integer>> inResourcesForEachProductions, Map<Integer, String> outResourcesForEachProductions) throws InvalidEventException, InvalidIndexException, NonStorableResourceException, EmptySlotException, NonAccessibleSlotException {
        return currentState.productionAction(inResourcesForEachProductions, outResourcesForEachProductions);
    }

    /**
     * Check if the player can place the card and then check if he can buy it with his discounts.
     * If yes buy the card and set the next State of the game to WaitDevelopmentCardPlacementState.
     *
     * @param cardColor         color of the card to buy
     * @param cardLevel         level of the card to buy
     * @param resourcePositions index of the chosen resources
     * @return true if the card has been successfully bought
     * @throws InvalidEventException      if the player can't buy the card
     * @throws InvalidIndexException      if one of the resource positions is negative
     * @throws EmptySlotException         if one of the resource slots is empty
     * @throws NonAccessibleSlotException if one of the resource position represents a slot that's not accessible
     */
    public boolean buyAction(String cardColor, int cardLevel, List<Integer> resourcePositions) throws InvalidEventException, InvalidIndexException, EmptySlotException, NonAccessibleSlotException {
        return currentState.buyAction(cardColor, cardLevel, resourcePositions);
    }

    /**
     * Activate or Discard a LeaderCard if the player has not done it yet.
     *
     * @param ID      of the chosen LeaderCard
     * @param discard true if the chosen LeaderCard has to be discarded, false if has to be activated
     * @return true if the leaderAction has been successfully applied
     * @throws InvalidEventException if the leaderAction can't be applied
     */
    public boolean leaderAction(String ID, boolean discard) throws InvalidEventException {
        return currentState.leaderAction(ID, discard);
    }

    /**
     * Reorder the warehouse and change the state of the game to EndTurnState. If the Player has some remaining resource
     * to store increases the FaithProgress of the other players.
     *
     * @param hasCompletedPlacementAction true if the player wishes for this place action to be final
     * @param swapPairs List of all the swaps to be applied
     * @return true if the warehouse reordering is legal
     * @throws InvalidEventException if the swaps cannot be applied
     */
    public boolean placeResourceAction(List<Integer> swapPairs, boolean hasCompletedPlacementAction) throws InvalidEventException{
        return currentState.placeResourceAction(swapPairs, hasCompletedPlacementAction);
    }

    /**
     * Place the chosenDevelopmentCard just bought into the given slot and change the State of the game to EndTurnState.
     *
     * @param slotPosition of the chosen production slot
     * @return if the card has been correctly placed
     * @throws InvalidEventException if the card can't be placed in the chosen slot
     */
    public boolean placeDevelopmentCardAction(int slotPosition) throws InvalidEventException {
        return currentState.placeDevelopmentCardAction(slotPosition);
    }

    /**
     * Add the chosen resources for the white resource transformation to the warehouse's market zone
     * and set the current state of the game to WaitResourcePlacementState.
     *
     * @param chosenColors of the chosen resources
     * @return true if the chosen resources has been correctly created
     * @throws InvalidEventException        if one of the chosen resource type doesn't exists
     * @throws NonStorableResourceException if one of the chosen resource is a NonStorableResource
     */
    public boolean transformationAction(List<String> chosenColors) throws InvalidEventException, NonStorableResourceException {
        return currentState.transformationAction(chosenColors);
    }

    /**
     * Check if there is a winner: if yes set the state of the game to EndGameState, else Lorenzo plays and re-check if
     * there is a winner. If yes re-set the state of the game to EndGameState, else set the next player and change
     * the state of the game to StartTurnState.
     *
     * @return true if there is a winner
     * @throws InvalidEventException if the Player cannot end the turn
     */
    public boolean endTurn() throws InvalidEventException {
        return currentState.endTurn();
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
     * getter of current state, used only in testing
     *
     * @return turnLogic currentState
     */
    public State getCurrentState() {
        return currentState;
    }


    /**
     * Saves the last event sent to the observers
     *
     * @param lastEventSent event to save
     */
    public void setLastEventSent(SendEvent lastEventSent) {
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
     *
     * @return if the player was the last one online
     */
    public boolean disconnectPlayer(String nickname) {
        Player disconnected = players.stream().filter(player -> player.getNickname().equals(nickname)).findFirst().orElse(null);
        assert disconnected != null;
        if (currentPlayer.equals(disconnected)) {
            currentPlayer.setDisconnectedData(currentState, whiteResourcesFromMarket, chosenDevCard, lastEventSent);
            disconnected.setOnline(false);
            if (players.stream().noneMatch(Player::isOnline)) {
                return true;
            } else {
                setNextPlayer();
                setCurrentState(startTurn);
                setLastEventSent(new StartTurnEvent(currentPlayer.getNickname(), currentPlayer.getNickname()));
            }
        }
        return false;
    }

    /**
     * This method is used to reconnect a player during the game
     *
     * @param nickname nickname of the player to reconnect
     */
    public void reconnectPlayer(String nickname) {
        Player reconnected = players.stream().filter(player -> player.getNickname().equals(nickname)).findFirst().orElse(null);
        GraphicUpdateEvent graphicsForReconnection = new GraphicUpdateEvent();
        graphicsForReconnection.addUpdate(new MarketUpdate());
        graphicsForReconnection.addUpdate(new GridUpdate());
        graphicsForReconnection.addUpdate(new FaithTracksUpdate());
        players.forEach(player -> graphicsForReconnection.addUpdate(new PersonalBoardUpdate(player, new FullProductionSlotsUpdate(), new LeaderCardSlotsUpdate(), new WarehouseUpdate())));
        modelInterface.notifyObservers(new ReconnectEvent(nickname, currentPlayer.getNickname(), players.stream().map(Player::getNickname).collect(Collectors.toList()), graphicsForReconnection));
        assert reconnected != null;
        reconnected.setOnline(true);
    }

    public boolean isIdle() {
        return currentState.equals(idle);
    }
}

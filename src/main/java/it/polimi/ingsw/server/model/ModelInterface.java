package it.polimi.ingsw.server.model;

import it.polimi.ingsw.commons.enums.ResourcesEnum;
import it.polimi.ingsw.server.events.send.EventToClient;
import it.polimi.ingsw.server.events.send.graphics.GraphicUpdateEvent;
import it.polimi.ingsw.server.events.send.graphics.PersonalBoardUpdate;
import it.polimi.ingsw.server.events.send.graphics.WarehouseUpdate;
import it.polimi.ingsw.server.exceptions.*;
import it.polimi.ingsw.server.model.gameBoard.GameBoard;
import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.server.model.resources.Resource;
import it.polimi.ingsw.server.model.resources.ResourceFactory;
import it.polimi.ingsw.server.model.turn.*;
import it.polimi.ingsw.server.utils.EventToClientObservable;
import it.polimi.ingsw.server.utils.EventToClientObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class represents the component Model of the Pattern MVC,
 * it contains all the information to describe correctly the application.
 * It is Observable by all the Virtual Views, it's purpose is to notify them when
 * there was a modify of the internal structure.
 */
public class ModelInterface implements EventToClientObservable {

    private final List<Player> players = new ArrayList<>();

    private final TurnLogic turnLogic;
    private final SetupManager setupManager;
    private final List<EventToClientObserver> virtualViews;

    private State currentState;
    protected final State startTurn, waitDevCardPlacement, waitTransformation, waitResourcePlacement, endTurn, endGame, idle;

    /**
     * This class contains all the methods visible to the controller in the MVC pattern
     * This constructor creates a new TurnLogic and setupManager with the given nicknames
     * It also resets the gameBoard in case it is not the first game played with this server
     *
     * @param nicknames nicknames of the player in the game
     */
    public ModelInterface(List<String> nicknames) {

        GameBoard.getGameBoard().reset();

        for (String username : nicknames) {
            this.players.add(new Player(username));
        }
        virtualViews = new ArrayList<>();

        turnLogic = new TurnLogic(players, this);

        setupManager = new SetupManager(players, this);

        this.startTurn = new StartTurnState(this);
        this.waitDevCardPlacement = new WaitDevelopmentCardPlacementState(this);
        this.waitTransformation = new WaitTransformationState(this);
        this.waitResourcePlacement = new WaitResourcePlacementState(this);
        this.endTurn = new EndTurnState(this);
        this.endGame = new EndGameState(this);
        this.idle = new IdleState(this);

        this.currentState = idle;
    }

    public State getCurrentState() {
        return currentState;
    }

    public void setCurrentState(State currentState) {
        this.currentState = currentState;
    }

    public State getStartTurn() {
        return startTurn;
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

    public State getEndGame() {
        return endGame;
    }

    public State getIdle() {
        return idle;
    }

    public List<Player> getPlayers() {
        return players;
    }


    /**
     * Get the turn manager of the current turn
     *
     * @return turnLogic
     */
    public TurnLogic getTurnLogic() {
        return turnLogic;
    }

    /**
     * Get the setup phase manager
     *
     * @return the Setup Manager
     */
    public SetupManager getSetupManager() {
        return setupManager;
    }

    /**
     * This method is used to add the Virtual View in the list of the Observer of this class
     *
     * @param virtualView object to add
     */
    @Override
    public void registerObserver(EventToClientObserver virtualView) {
        virtualViews.add(virtualView);
    }

    /**
     * This method notify the Virtual Views of an amendment of the Model
     *
     * @param eventToClient the Event from the Model
     */
    @Override
    public void notifyObservers(EventToClient eventToClient) {
        virtualViews.forEach(view -> view.update(eventToClient));
    }

    /**
     * Add the chosen LeaderCards and Resources to the Player's board in the setup phase.
     *
     * @param nickname          of the Player
     * @param leaderCardIndexes of the chosen LeaderCards by the Player
     * @param resources         chosen by the Player
     * @return true if the choices are correct
     * @throws InvalidEventException if the choices aren't correct
     * @throws InvalidSetupException if setup failed
     */
    public boolean setupAction(String nickname, List<Integer> leaderCardIndexes, List<String> resources) throws InvalidEventException, InvalidSetupException {
        return setupManager.setupAction(nickname, leaderCardIndexes, resources);
    }


    /**
     * Take the chosen resources from the MarketTray and set the current state of the game to
     * WaitResourceTransformation if there are some White Resources to transform or else to
     * WaitResourcePlacementState.
     *
     * @param arrowID is the index of the chosen line of the MarketTray
     * @return true if the state has been changed
     * @throws InvalidIndexException if the arrowID is not correct/**
     * @throws InvalidEventException if the action failed
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
     * @throws EmptySlotException           if the first slot in at least one of the swap pairs is empty
     * @throws NonAccessibleSlotException   if one of the selected slot is not accessible for this action
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
     * @param cardID  of the chosen LeaderCard
     * @param discard true if the chosen LeaderCard has to be discarded, false if has to be activated
     * @return true if the leaderAction has been successfully applied
     * @throws InvalidEventException if the leaderAction can't be applied
     */
    public boolean leaderAction(String cardID, boolean discard) throws InvalidEventException {
        return currentState.leaderAction(cardID, discard);
    }

    /**
     * Reorder the warehouse and change the state of the game to EndTurnState. If the Player has some remaining resource
     * to store increases the FaithProgress of the other players.
     *
     * @param swapPairs                        List of all the swaps to be applied
     * @param isFinal true if the Player wants to confirm the reordering
     * @return true if the warehouse reordering is legal
     * @throws InvalidEventException if the swaps cannot be applied
     */
    public boolean placeResourceAction(List<Integer> swapPairs, boolean isFinal) throws InvalidEventException {
        return currentState.placeResourceAction(swapPairs, isFinal);
    }

    /**
     * Place the chosenDevelopmentCard just bought into the given slot and change the State of the game to EndTurnState.
     *
     * @param slotPosition of the chosen production slot
     * @return true if the card has been correctly placed
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
     * This method return the object Player given his nickname
     *
     * @param nickname is the nickname of the Player
     * @return the instance of the Player
     */
    public Player getPlayerByNickname(String nickname) {
        return players.stream().filter(player -> player.getNickname().equals(nickname)).findFirst()
                .orElse(null);
    }

    /**
     * Start the setup phase of the game
     */
    public void startSetup() {
        setupManager.startSetup();
    }

    /**
     * Resend the last event the model produced in case of an illegalAction
     */
    public void reSendLastEvent() {
        turnLogic.reSendLastEvent();
    }

    /**
     * Resend a SetupEvent in case of an illegal SetupAction
     *
     * @param nickname player that will receive the setupEvent again
     */
    public void reSendSetupEventFor(String nickname) {
        setupManager.resendSetupEventFor(nickname);
    }

    /**
     * Set a player in the model as offline and save all its data.
     *
     * @param nickname of the disconnected player
     * @return true if the player was the last one online
     */
    public boolean disconnectPlayer(String nickname) {
        return currentState.disconnectAction(nickname);
    }

    /**
     * Set a player in the model as online and reload all its data.
     *
     * @param nickname of the player reconnected
     * @return true
     */
    public boolean reconnectPlayer(String nickname) {
        currentState.reconnectAction(nickname);
        return true;
    }

    /**
     * Cheat 6 resources of each types to each player during the game demo
     * It does so by adding them to each player's Strong Box
     */
    public void cheat() {
        List<Resource> cheatResources = new ArrayList<>();
        for (ResourcesEnum resourceEnum : ResourcesEnum.values()) {
            for (int i = 0; i < 6; i++) {
                try {
                    cheatResources.add(ResourceFactory.produceResource(resourceEnum));
                } catch (NonStorableResourceException ignored) {
                }
            }
        }
        players.forEach(p -> p.getPersonalBoard().getWarehouse().addResourcesToStrongBox(cheatResources));
        GraphicUpdateEvent graphicUpdateEvent = new GraphicUpdateEvent();
        players.forEach(p -> graphicUpdateEvent.addUpdate(new PersonalBoardUpdate(p, new WarehouseUpdate())));
        notifyObservers(graphicUpdateEvent);
    }

    /**
     * This method loads the initial MarketTray,Development Cards Grid and DeckLeaders data
     * saved in the Json Files during previous Games.
     */
    public void loadInitialGameBoardData() {
        turnLogic.getGameMode().getLorenzo().loadSavedTokens();
        GameBoard.getGameBoard().getDevelopmentCardsGrid().loadSavedData();
        GameBoard.getGameBoard().getDeckLeader().loadSavedData();
        GameBoard.getGameBoard().getMarketTray().loadSavedData();
    }

    /**
     * This method saves the MarketTray,Development Cards Grid and DeckLeaders data
     * in the appropriate Json Files.
     */
    public void saveInitialGameBoardData() {
        GameBoard.getGameBoard().getDevelopmentCardsGrid().saveData();
        GameBoard.getGameBoard().getDeckLeader().saveData();
        GameBoard.getGameBoard().getMarketTray().saveData();
    }

    /**
     * This method send the necessary events to the client in order for them to restart they game where they left it
     */
    public void sendNecessaryEvents() {
        currentState.sendNecessaryEvents();
    }

    public void loadDefaultTokens() {
        turnLogic.getGameMode().getLorenzo().generateNormalTokens();
    }
}
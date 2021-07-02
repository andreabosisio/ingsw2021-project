package it.polimi.ingsw.server.model.turn;

import it.polimi.ingsw.server.exceptions.*;
import it.polimi.ingsw.server.model.ModelInterface;
import it.polimi.ingsw.server.model.player.Player;

import java.util.List;
import java.util.Map;

/**
 * Abstract implementation of a Game State. It contains all the possible game actions.
 */
public abstract class State {

    final ModelInterface modelInterface;
    final TurnLogic turnLogic;

    private final String INVALID_ACTION_MSG = "Invalid action right now!";

    /**
     * Create a State by setting the Model reference.
     *
     * @param modelInterface Model reference
     */
    public State(ModelInterface modelInterface) {
        this.modelInterface = modelInterface;
        this.turnLogic = modelInterface.getTurnLogic();
    }

    public Player getCurrentPlayer() {
        return modelInterface.getTurnLogic().getCurrentPlayer();
    }

    /**
     * Take the chosen resources from the MarketTray and set the current state of the game to
     * WaitResourceTransformation if there are some White Resources to transform or else to
     * WaitResourcePlacementState.
     *
     * @param arrowID is the index of the chosen line of the MarketTray
     * @return true if the state has been changed
     * @throws InvalidIndexException if the arrowID is not correct
     * @throws InvalidEventException if done during invalid game state
     */
    public boolean marketAction(int arrowID) throws InvalidEventException, InvalidIndexException {
        throw new InvalidEventException(INVALID_ACTION_MSG);
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
     * @throws EmptySlotException if one of the chosen resource position is empty
     * @throws NonAccessibleSlotException if one of the chosen resource position is non accessible
     */
    public boolean productionAction(Map<Integer, List<Integer>> inResourcesForEachProductions, Map<Integer, String> outResourcesForEachProductions) throws InvalidEventException, InvalidIndexException, NonStorableResourceException, EmptySlotException, NonAccessibleSlotException {
        throw new InvalidEventException(INVALID_ACTION_MSG);
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
        throw new InvalidEventException(INVALID_ACTION_MSG);
    }

    /**
     * Activate or Discard a LeaderCard if the player has not done it yet.
     *
     * @param ID  of the chosen LeaderCard
     * @param discard true if the chosen LeaderCard has to be discarded, false if has to be activated
     * @return true if the leaderAction has been successfully applied
     * @throws InvalidEventException if the leaderAction can't be applied
     */
    public boolean leaderAction(String ID, boolean discard) throws InvalidEventException {
        throw new InvalidEventException(INVALID_ACTION_MSG);
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
        throw new InvalidEventException(INVALID_ACTION_MSG);
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
        throw new InvalidEventException(INVALID_ACTION_MSG);
    }

    /**
     * Place the chosenDevelopmentCard just bought into the given slot and change the State of the game to EndTurnState.
     *
     * @param slotPosition of the chosen production slot
     * @return true if the card has been correctly placed
     * @throws InvalidEventException if the card can't be placed in the chosen slot
     */
    public boolean placeDevelopmentCardAction(int slotPosition) throws InvalidEventException {
        throw new InvalidEventException(INVALID_ACTION_MSG);
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
        throw new InvalidEventException(INVALID_ACTION_MSG);
    }

    /**
     * Set a player in the model as offline and save all its data.
     *
     * @param nickname of the disconnected player
     * @return true if the player was the last one online
     */
    public boolean disconnectAction(String nickname) {
        return turnLogic.disconnectPlayer(nickname);
    }

    /**
     * Set a player in the model as online and reload all its data.
     *
     * @param nickname of the player reconnected
     */
    public void reconnectAction(String nickname) {
        turnLogic.reconnectPlayer(nickname);
    }

    /**
     * This method send the necessary events to the client in order for them to restart they game where they left it
     */
    public void sendNecessaryEvents() {
        turnLogic.reSendLastEvent();
    }

}

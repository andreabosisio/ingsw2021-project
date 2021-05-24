package it.polimi.ingsw.server.model;

import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.server.events.send.SendEvent;
import it.polimi.ingsw.server.events.send.choice.SetupChoiceEvent;
import it.polimi.ingsw.server.model.gameBoard.GameBoard;
import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.server.model.turn.TurnLogic;
import it.polimi.ingsw.server.utils.SendObservable;
import it.polimi.ingsw.server.utils.SendObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ModelInterface implements SendObservable {

    private final List<Player> players = new ArrayList<>();
    private final TurnLogic turnLogic;
    private final SetupManager setupManager;
    private final List<SendObserver> virtualViews;

    public ModelInterface(List<String> nicknames) {

        GameBoard.getGameBoard().reset();

        for (String username : nicknames) {
            this.players.add(new Player(username));
        }
        virtualViews = new ArrayList<>();

        turnLogic = new TurnLogic(players, this);

        setupManager = new SetupManager(players, this);
    }

    /**
     * Getter of current player's nickname
     *
     * @return nickname of current player
     */
    public String getCurrentPlayerNickname() {
        return turnLogic.getCurrentPlayer().getNickname();
    }

    /**
     * Get information of the current turn
     *
     * @return turnLogic
     */
    public TurnLogic getTurnLogic() {
        return turnLogic;
    }

    /**
     * Add the chosen LeaderCards and Resources to the Player's board in the setup phase.
     *
     * @param nickname          of the Player
     * @param leaderCardIndexes of the chosen LeaderCards by the Player
     * @param resources         chosen by the Player
     * @return true if the choices are correct
     * @throws InvalidEventException        if the choices aren't correct
     * @throws NonStorableResourceException if Player choose a NonStorableResource
     */
    public boolean setupAction(String nickname, List<Integer> leaderCardIndexes, List<String> resources) throws InvalidEventException, NonStorableResourceException, InvalidSetupException {
        return setupManager.setupAction(nickname, leaderCardIndexes, resources);
    }

    /**
     * Take the chosen resources from the MarketTray and set the current state of the game to
     * WaitResourceTransformation if there are some White Resources to transform or else to
     * WaitResourcePlacementState.
     *
     * @param arrowID is the index of the chosen line of the MarketTray
     * @return true if the state has been changed
     * @throws InvalidIndexException if the arrowID is not correct
     */
    public boolean marketAction(int arrowID) throws InvalidEventException, InvalidIndexException {
        return turnLogic.marketAction(arrowID);
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
     */
    public boolean productionAction(Map<Integer, List<Integer>> inResourcesForEachProductions, Map<Integer, String> outResourcesForEachProductions) throws InvalidEventException, InvalidIndexException, NonStorableResourceException, EmptySlotException, NonAccessibleSlotException {
        return turnLogic.productionAction(inResourcesForEachProductions, outResourcesForEachProductions);
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
        return turnLogic.buyAction(cardColor, cardLevel, resourcePositions);
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
        return turnLogic.leaderAction(cardID, discard);
    }

    /**
     * Reorder the warehouse and change the state of the game to EndTurnState. If the Player has some remaining resource
     * to store increases the FaithProgress of the other players.
     *
     * @param swapPairs List of all the swaps to be applied
     * @return true if the warehouse reordering is legal
     * @throws InvalidEventException      if the swaps cannot be applied
     * @throws InvalidIndexException      if a swap contains a negative position
     * @throws EmptySlotException         if a swap involves an empty slot
     * @throws NonAccessibleSlotException if one of swap involves a slot that's not accessible
     */
    public boolean placeResourceAction(List<Integer> swapPairs, boolean hasCompletedTransformationAction) throws InvalidEventException, InvalidIndexException, EmptySlotException, NonAccessibleSlotException {
        return turnLogic.placeResourceAction(swapPairs, hasCompletedTransformationAction);
    }

    /**
     * Place the chosenDevelopmentCard just bought into the given slot and change the State of the game to EndTurnState.
     *
     * @param slotPosition of the chosen production slot
     * @return if the card has been correctly placed
     * @throws InvalidEventException if the card can't be placed in the chosen slot
     */
    public boolean placeDevelopmentCardAction(int slotPosition) throws InvalidEventException {
        return turnLogic.placeDevelopmentCardAction(slotPosition);
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
        return turnLogic.transformationAction(chosenColors);
    }

    /**
     * Check if there is a winner: if yes set the state of the game to EndGameState, else Lorenzo plays and re-check if
     * there is a winner. If yes re-set the state of the game to EndGameState, else set the next player and change
     * the state of the game to StartTurnState.
     *
     * @return true if there is a winner
     */
    public boolean endTurn() throws InvalidEventException {
        return turnLogic.endTurn();
    }

    @Override
    public void registerObserver(SendObserver virtualView) {
        virtualViews.add(virtualView);
    }

    @Override
    public void removeObserver(SendObserver virtualView) {
        int i = virtualViews.indexOf(virtualView);
        if (i >= 0) {
            virtualViews.remove(virtualView);
        }
    }

    /**
     * This method notify the Virtual Views of an amendment of the Model
     *
     * @param sendEvent the Event from the Model
     */
    @Override
    public void notifyObservers(SendEvent sendEvent) {
        virtualViews.forEach(view -> view.update(sendEvent));
    }

    public Player getPlayerByNickname(String nickname) {
        return players.stream().filter(player -> player.getNickname().equals(nickname)).findFirst()
                .orElse(null);
    }

    /**
     * This method is to start the setup phase of the game
     */
    public void startSetup() {
        setupManager.startSetup();
    }

    /**
     * This method is used to resend the last event the model produced in case of an illegalAction
     */
    public void reSendLastEvent(){turnLogic.reSendLastEvent();}

    /**
     * This method is used to resent a setupEvent in case of an illegal SetupAction
     *
     * @param nickname player that will receive the setupEvent again
     */
    public void reSendSetup(String nickname){
       for(SetupChoiceEvent event :setupManager.getSetupSendEvents()){
           if(event.getNickname().equals(nickname)){
               notifyObservers(event);
           }
       }

    }

    /**
     * This method set a player in the model as offline
     * @param nickname offline player
     */
    public void setPlayerOffline(String nickname){
        getPlayerByNickname(nickname).setOnline(false);
        //todo check if currentPlayer is the one disconnected if yes decide what to do
    }
}
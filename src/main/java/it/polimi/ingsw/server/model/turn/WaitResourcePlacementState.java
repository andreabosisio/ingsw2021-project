package it.polimi.ingsw.server.model.turn;

import it.polimi.ingsw.exceptions.EmptySlotException;
import it.polimi.ingsw.exceptions.InvalidEventException;
import it.polimi.ingsw.exceptions.InvalidIndexException;
import it.polimi.ingsw.exceptions.NonAccessibleSlotException;
import it.polimi.ingsw.server.events.send.choice.EndTurnChoiceEvent;
import it.polimi.ingsw.server.events.send.choice.PlaceResourcesChoiceEvent;
import it.polimi.ingsw.server.events.send.graphics.FaithTracksUpdate;
import it.polimi.ingsw.server.events.send.graphics.GraphicUpdateEvent;
import it.polimi.ingsw.server.events.send.graphics.PersonalBoardUpdate;
import it.polimi.ingsw.server.model.gameBoard.GameBoard;
import it.polimi.ingsw.server.model.player.warehouse.Warehouse;
import java.util.List;

public class WaitResourcePlacementState extends State {
    public WaitResourcePlacementState(TurnLogic turnLogic) {
        super(turnLogic);
    }

    /**
     * Reorder the warehouse and change the state of the game to EndTurnState. If the Player has some remaining resource
     * to store increases the FaithProgress of the other players.
     *
     * @param swapPairs List of all the swaps to be applied
     * @param hasCompletedPlacementAction true if player wish for this reordering to be final
     * @return true if the warehouse reordering is legal
     * @throws InvalidEventException      if the swaps cannot be applied
     */
    @Override
    public boolean placeResourceAction(List<Integer> swapPairs, boolean hasCompletedPlacementAction) throws InvalidEventException {
        if (swapPairs.size() % 2 != 0) {
            //resend place choice
            turnLogic.setLastEventSent(new PlaceResourcesChoiceEvent(turnLogic.getCurrentPlayer().getNickname()));
            throw new InvalidEventException("Every swap should always have an initial position and a final position"); //a swap should always have an initPosition and a finalPosition
        }
        Warehouse warehouse = turnLogic.getCurrentPlayer().getPersonalBoard().getWarehouse();

        for (int i = 0; i < swapPairs.size(); i = i + 2) {
            try{
                //if a swap is not legal invalidate decision to end swaps
                if(!warehouse.swap(swapPairs.get(i), swapPairs.get(i + 1))){
                    sendWarehouseUpdate();
                    turnLogic.setLastEventSent(new PlaceResourcesChoiceEvent(turnLogic.getCurrentPlayer().getNickname()));
                    throw new InvalidEventException("Some swaps could not be applied"); //the swap cannot be applied
                }
            }catch ( InvalidIndexException|EmptySlotException|NonAccessibleSlotException e){
                //if a swap generated an exception send the new warehouse configuration
                sendWarehouseUpdate();
                turnLogic.setLastEventSent(new PlaceResourcesChoiceEvent(turnLogic.getCurrentPlayer().getNickname()));
                throw new InvalidEventException("Action failed because: " + e.getMessage()); //the swap cannot be applied
            }
        }
        if (hasCompletedPlacementAction && warehouse.isProperlyOrdered()) {
            //faith progress for other players based on the number of remaining resources
            int remainingResources = warehouse.getNumberOfRemainingResources();
            GameBoard.getGameBoard().faithProgressForOtherPlayers(turnLogic.getCurrentPlayer(), remainingResources);

            //graphic update of player's warehouse and players faithTracks
            GraphicUpdateEvent graphicUpdateEvent = new GraphicUpdateEvent();
            graphicUpdateEvent.addUpdate(new FaithTracksUpdate());
            graphicUpdateEvent.addUpdate(new PersonalBoardUpdate(turnLogic.getCurrentPlayer().getNickname(), turnLogic.getCurrentPlayer().getPersonalBoard().getWarehouse()));
            if (remainingResources > 0)
                graphicUpdateEvent.addUpdate("Your Faith Marker is moving by " + remainingResources + " positions because " + turnLogic.getCurrentPlayer().getNickname() + " is not able to reorder his warehouse!");
            turnLogic.getModelInterface().notifyObservers(graphicUpdateEvent);
            EndTurnChoiceEvent endTurnChoiceEvent = new EndTurnChoiceEvent(turnLogic.getCurrentPlayer().getNickname());
            turnLogic.setLastEventSent(endTurnChoiceEvent);
            turnLogic.getModelInterface().notifyObservers(endTurnChoiceEvent);
            turnLogic.setCurrentState(turnLogic.getEndTurn());
            return true;
        }

        //graphic update of player's illegal/not final warehouse
        sendWarehouseUpdate();

        PlaceResourcesChoiceEvent placeResourcesReceiveEvent = new PlaceResourcesChoiceEvent(turnLogic.getCurrentPlayer().getNickname());
        turnLogic.setLastEventSent(placeResourcesReceiveEvent);
        //if not final simply resend the choice event
        if(!hasCompletedPlacementAction){
            turnLogic.getModelInterface().notifyObservers(placeResourcesReceiveEvent);
            return true;
        }
        //if final send an error message of illegal warehouse reordering
        throw new InvalidEventException("Illegal Warehouse reordering");
    }

    private void sendWarehouseUpdate(){
        GraphicUpdateEvent graphicUpdateEvent = new GraphicUpdateEvent();
        graphicUpdateEvent.addUpdate(new PersonalBoardUpdate(turnLogic.getCurrentPlayer().getNickname(), turnLogic.getCurrentPlayer().getPersonalBoard().getWarehouse()));
        turnLogic.getModelInterface().notifyObservers(graphicUpdateEvent);
    }
}

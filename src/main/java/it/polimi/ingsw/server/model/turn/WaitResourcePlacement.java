package it.polimi.ingsw.server.model.turn;

import it.polimi.ingsw.exceptions.EmptySlotException;
import it.polimi.ingsw.exceptions.InvalidEventException;
import it.polimi.ingsw.exceptions.InvalidIndexException;
import it.polimi.ingsw.exceptions.NonAccessibleSlotException;
import it.polimi.ingsw.server.model.gameBoard.GameBoard;
import it.polimi.ingsw.server.model.player.Warehouse;

import java.util.List;

public class WaitResourcePlacement extends State {
    public WaitResourcePlacement(TurnLogic turnLogic) {
        super(turnLogic);
    }

    /**
     * Reorder the warehouse and change the state of the game to EndTurn. If the Player has some remaining resource
     * to store increases the FaithProgress of the other players.
     *
     * @param swapPairs List of all the swaps to be applied
     * @return true if the warehouse reordering is legal
     * @throws InvalidEventException if the swaps cannot be applied
     * @throws InvalidIndexException if a swap contains a negative position
     * @throws EmptySlotException if a swap involves an empty slot
     * @throws NonAccessibleSlotException if one of swap involves a slot that's not accessible
     */
    //todo ricordarsi che le risorse dal market possono essere anche tolte (in caso di not legal)
    @Override
    public boolean placeResourceAction(List<Integer> swapPairs) throws InvalidEventException, InvalidIndexException, EmptySlotException, NonAccessibleSlotException {
        if(swapPairs.size() % 2 != 0)
            throw new InvalidEventException(); //a swap should always have an initPosition and a finalPosition

        Warehouse warehouse = turnLogic.getCurrentPlayer().getPersonalBoard().getWarehouse();

        for(int i = 0; i < swapPairs.size(); i = i + 2)
            if(!warehouse.swap(swapPairs.get(i),swapPairs.get(i + 1)))
                throw new InvalidEventException(); //the swap cannot be applied

        if(warehouse.isProperlyOrdered()) {
            //faith progress for other players based on the number of remaining resources
            GameBoard.getGameBoard().faithProgressForOtherPlayers(turnLogic.getCurrentPlayer(), warehouse.getNumberOfRemainingResources());
            turnLogic.setCurrentState(turnLogic.getEndTurn());
            return true;
        }
        return false;
    }
}

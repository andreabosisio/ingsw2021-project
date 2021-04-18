package it.polimi.ingsw.server.model.turn;

import it.polimi.ingsw.exceptions.InvalidEventException;

public class WaitDevCardPlacement extends State {
    public WaitDevCardPlacement(TurnLogic turnLogic) {
        super(turnLogic);
    }

    /**
     * Place the chosenDevCard into the given slot and change the State of the game to EndTurn.
     *
     * @param slotPosition of the chosen production slot
     * @return if the card has been correctly placed
     * @throws InvalidEventException if the card can't be placed in the chosen slot
     */
    @Override
    public boolean placeDevCardAction(int slotPosition) throws InvalidEventException {
        if(turnLogic.getCurrentPlayer().getPersonalBoard().setNewDevCard(slotPosition, turnLogic.getChosenDevCard())) {
            turnLogic.setCurrentState(turnLogic.getEndTurn());
            return true;
        }
        throw new InvalidEventException();
    }
}

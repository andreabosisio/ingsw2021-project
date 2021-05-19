package it.polimi.ingsw.server.model.turn;

import it.polimi.ingsw.exceptions.InvalidEventException;
import it.polimi.ingsw.server.events.send.graphics.GraphicUpdateEvent;
import it.polimi.ingsw.server.events.send.graphics.PersonalBoardUpdate;

public class WaitDevelopmentCardPlacement extends State {
    public WaitDevelopmentCardPlacement(TurnLogic turnLogic) {
        super(turnLogic);
    }

    /**
     * Place the chosenDevCard just bought into the given slot and change the State of the game to EndTurn.
     *
     * @param slotPosition of the chosen production slot
     * @return if the card has been correctly placed
     * @throws InvalidEventException if the card can't be placed in the chosen slot
     */
    @Override
    public boolean placeDevelopmentCardAction(int slotPosition) throws InvalidEventException {
        if(turnLogic.getCurrentPlayer().getPersonalBoard().setNewDevelopmentCard(slotPosition, turnLogic.getChosenDevCard())) {

            //graphic update of player's DevCards owned
            GraphicUpdateEvent graphicUpdateEvent = new GraphicUpdateEvent();
            graphicUpdateEvent.addUpdate(new PersonalBoardUpdate(turnLogic.getCurrentPlayer().getNickname(),turnLogic.getCurrentPlayer().getPersonalBoard()));
            turnLogic.getModelInterface().notifyObservers(graphicUpdateEvent);
            turnLogic.setLastEventSent(null);
            turnLogic.setCurrentState(turnLogic.getEndTurn());
            return true;
        }
        throw new InvalidEventException("card placement failed");
    }
}

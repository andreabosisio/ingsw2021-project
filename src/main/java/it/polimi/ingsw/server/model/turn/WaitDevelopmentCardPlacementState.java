package it.polimi.ingsw.server.model.turn;

import it.polimi.ingsw.exceptions.InvalidEventException;
import it.polimi.ingsw.server.events.send.choice.EndTurnChoiceEvent;
import it.polimi.ingsw.server.events.send.graphics.GraphicUpdateEvent;
import it.polimi.ingsw.server.events.send.graphics.PersonalBoardUpdate;

public class WaitDevelopmentCardPlacementState extends State {
    public WaitDevelopmentCardPlacementState(TurnLogic turnLogic) {
        super(turnLogic);
    }

    /**
     * Place the chosenDevCard just bought into the given slot and change the State of the game to EndTurnState.
     *
     * @param slotPosition of the chosen production slot
     * @return if the card has been correctly placed
     * @throws InvalidEventException if the card can't be placed in the chosen slot
     */
    @Override
    public boolean placeDevelopmentCardAction(int slotPosition) throws InvalidEventException {
        if(turnLogic.getCurrentPlayer().getPersonalBoard().setNewProductionCard(slotPosition, turnLogic.getChosenDevCard())) {

            //graphic update of player's DevCards owned
            GraphicUpdateEvent graphicUpdateEvent = new GraphicUpdateEvent();
            graphicUpdateEvent.addUpdate(new PersonalBoardUpdate(turnLogic.getCurrentPlayer().getNickname(),turnLogic.getCurrentPlayer().getPersonalBoard()));
            graphicUpdateEvent.addUpdate(turnLogic.getCurrentPlayer().getNickname() + " is placing his new card...");
            turnLogic.getModelInterface().notifyObservers(graphicUpdateEvent);
            EndTurnChoiceEvent endTurnChoiceEvent = new EndTurnChoiceEvent(turnLogic.getCurrentPlayer().getNickname());
            turnLogic.setLastEventSent(endTurnChoiceEvent);
            turnLogic.getModelInterface().notifyObservers(endTurnChoiceEvent);
            turnLogic.setCurrentState(turnLogic.getEndTurn());
            return true;
        }
        throw new InvalidEventException("Cannot place the card in the chosen slot");
    }
}

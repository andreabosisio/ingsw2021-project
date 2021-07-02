package it.polimi.ingsw.server.model.turn;

import it.polimi.ingsw.server.events.send.choice.EndTurnChoiceEvent;
import it.polimi.ingsw.server.events.send.graphics.GraphicUpdateEvent;
import it.polimi.ingsw.server.events.send.graphics.PersonalBoardUpdate;
import it.polimi.ingsw.server.events.send.graphics.ProductionSlotsUpdate;
import it.polimi.ingsw.server.exceptions.InvalidEventException;
import it.polimi.ingsw.server.model.ModelInterface;

/**
 * State of the Model that accepts only Development Cards Placement
 */
public class WaitDevelopmentCardPlacementState extends State {

    public WaitDevelopmentCardPlacementState(ModelInterface modelInterface) {
        super(modelInterface);
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
        if (getCurrentPlayer().getPersonalBoard().setNewProductionCard(slotPosition, turnLogic.getChosenDevCard())) {

            //graphic update of player's DevCards owned
            GraphicUpdateEvent graphicUpdateEvent = new GraphicUpdateEvent();
            graphicUpdateEvent.addUpdate(new PersonalBoardUpdate(getCurrentPlayer(), new ProductionSlotsUpdate()));
            graphicUpdateEvent.addUpdate(getCurrentPlayer().getNickname() + " is placing his new card...");
            modelInterface.notifyObservers(graphicUpdateEvent);
            EndTurnChoiceEvent endTurnChoiceEvent = new EndTurnChoiceEvent(getCurrentPlayer().getNickname());
            turnLogic.setLastEventSent(endTurnChoiceEvent);
            modelInterface.notifyObservers(endTurnChoiceEvent);
            modelInterface.setCurrentState(modelInterface.getEndTurn());
            return true;
        }
        throw new InvalidEventException("Cannot place the card in the chosen slot");
    }
}

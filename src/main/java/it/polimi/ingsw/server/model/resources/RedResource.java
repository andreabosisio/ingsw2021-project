package it.polimi.ingsw.server.model.resources;

import it.polimi.ingsw.commons.enums.ResourcesEnum;
import it.polimi.ingsw.server.model.gameBoard.GameBoard;
import it.polimi.ingsw.server.model.turn.TurnLogic;

/**
 * Resource that make a progress on the FaithTrack.
 * MarketAbility and ProductionAbility are the same for the RedResource.
 * Both of them move the FaithMarker of the current player by faithProgressSteps steps.
 * For this reason the ProductionAbility call a MarketAbility.
 */
public class RedResource extends Resource {

    private final int faithProgressSteps = 1;

    public RedResource() {
        super(ResourcesEnum.RED);
    }

    /**
     * Method to call after a Resource has been chosen in the MarketTray.
     * Moves the FaithMarker of the current player by faithProgressSteps steps.
     *
     * @param turn containing the current player, the current state of the game and others information
     * @return true if the progress has been done successfully
     */
    @Override
    public boolean marketAbility(TurnLogic turn) {
        GameBoard.getGameBoard().faithProgress(turn.getCurrentPlayer(), faithProgressSteps);
        return true;
    }

    /**
     * Method to call after a Resource has been produced by a ProductionAction of a ProductionCard.
     * Moves the FaithMarker of the current player by faithProgressSteps steps.
     *
     * @param turn containing the current player, the current state of the game and others information
     * @return true if the progress has been done successfully
     */
    @Override
    public boolean productionAbility(TurnLogic turn) {
        return marketAbility(turn);
    }

}

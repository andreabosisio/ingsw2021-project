package it.polimi.ingsw.server.model.resources;

import it.polimi.ingsw.commons.enums.ResourcesEnum;
import it.polimi.ingsw.server.model.gameBoard.GameBoard;
import it.polimi.ingsw.server.model.turn.TurnLogic;

/**
 * Resources that can be used to make a production by the ProductionCard cards
 * and that can be used to buy/activate cards.
 * Resource of this type are:
 * GRAY,
 * YELLOW,
 * PURPLE,
 * BLUE
 */
public class StorableResource extends Resource {

    public StorableResource(ResourcesEnum color) {
        super(color);
    }

    /**
     * Method to call after a Resource has been chosen in the MarketTray.
     * Create a new Resource based on the color of this Resource.
     *
     * @param turn containing the current player, the current state of the game and others information
     * @return true if the new Resource has been created and added successfully
     * to the list of the new resources in MarketTray
     */
    @Override
    public boolean marketAbility(TurnLogic turn) {
        return GameBoard.getGameBoard().getMarketTray().addNewResource(new StorableResource(this.getColor()));
    }

    /**
     * Method to call after a Resource has been produced by a ProductionAction of a ProductionCard.
     * Create a new Resource based on the color of this Resource.
     *
     * @param turn containing the current player, the current state of the game and others information
     * @return true if the new Resource has been successfully stored in the StrongBox of the current player
     */
    @Override
    public boolean productionAbility(TurnLogic turn) {
        return turn.getCurrentPlayer().getPersonalBoard().getWarehouse().addResourcesToStrongBox(new StorableResource(this.getColor()));
    }

}

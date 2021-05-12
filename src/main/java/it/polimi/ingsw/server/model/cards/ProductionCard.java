package it.polimi.ingsw.server.model.cards;

import it.polimi.ingsw.server.model.resources.Resource;
import it.polimi.ingsw.server.model.turn.TurnLogic;

import java.util.List;

/**
 * Interface of the cards that offer a ProductionAction of resources.
 */

public interface ProductionCard {

    /**
     * Activate the production of the card.
     *
     * @param turnLogic turn
     * @return true if the production has been applied correctly
     */
    boolean usePower(TurnLogic turnLogic);

    /**
     * Getter of the Resources required to activate the production of the card.
     *
     * @return a list of resources
     */
    List<Resource> getInResources();

    /**
     * Getter of the Resources provided by the production of the Card.
     *
     * @return a list of resources
     */
    List<Resource> getOutResources();

    /**
     * Check if the givenResources satisfy the production requirements of the card.
     *
     * @param givenResources from a player
     * @return true if the givenResources satisfy the production requirements of the card
     */
    boolean canDoProduction(List<Resource> givenResources);

    /**
     * Return card's victory points.
     *
     * @return victory points of the card
     */
    int getPoints();

    /**
     * Return card's ID
     *
     * @return ID of the card
     */
    String getID();
}

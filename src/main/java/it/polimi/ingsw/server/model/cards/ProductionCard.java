package it.polimi.ingsw.server.model.cards;

import it.polimi.ingsw.exceptions.NonStorableResourceException;
import it.polimi.ingsw.server.model.resources.Resource;

import java.util.List;

/**
 * Interface of the cards that offer a ProductionAction of resources.
 */

public interface ProductionCard {

    /**
     * Activate the production of the card.
     * @return a list with the resources just produced
     */
     List<Resource> usePower();

    /**
     * Getter of the Resources required to activate the production of the card.
     * @return a list of resources
     */
    List<Resource> getInResources();

    /**
     * Getter of the Resources provided by the production of the Card.
     * @return a list of resources
     */
    List<Resource> getOutResources();

    /**
     * Check if the givenResources satisfy the production requirements of the card.
     * @param givenResources  from a player
     * @return true if the givenResources satisfy the production requirements of the card
     */
    boolean canDoProduction(List<Resource> givenResources) throws NonStorableResourceException;
}

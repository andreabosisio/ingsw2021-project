package it.polimi.ingsw.server.model.player.warehouse;

import it.polimi.ingsw.server.exceptions.EmptySlotException;
import it.polimi.ingsw.server.model.resources.Resource;


public interface ResourcesContainer {

    /**
     * Add the resource toStock into the slot defined by the given position.
     *
     * @param position of the chosen slot
     * @param toStock  Resource to stock
     * @return true if the Resource has been correctly stocked
     */
    boolean setResource(int position, Resource toStock);

    /**
     * Take and remove the resource stored into the slot defined by the given position.
     *
     * @param position of the chosen slot
     * @return the taken Resource
     * @throws EmptySlotException If the selected slot was empty
     */
    Resource takeResource(int position) throws EmptySlotException;

    /**
     * Get the resource stored into the slot defined by the given position without removing it from the Warehouse.
     *
     * @param position of the chosen slot
     * @return a copy of the chosen Resource
     */
    Resource getResource(int position);

}

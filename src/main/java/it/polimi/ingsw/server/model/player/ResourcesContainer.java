package it.polimi.ingsw.server.model.player;

import it.polimi.ingsw.exceptions.EmptySlotException;
import it.polimi.ingsw.server.model.resources.Resource;


public interface ResourcesContainer {

    /**
     * Add the resource toStock into the slot defined by the given position.
     *
     * @param position of the chosen slot
     * @param toStock           Resource to stock
     * @return true if the Resource has been correctly stocked
     */
    boolean setResource(int position, Resource toStock);

    /**
     * Remove the resource stored into the slot defined by the given position.
     *
     * @param position of the chosen slot
     * @return the taken Resource
     */
    Resource getResource(int position) throws EmptySlotException;
}

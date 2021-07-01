package it.polimi.ingsw.server.model.player.warehouse;

import it.polimi.ingsw.server.exceptions.EmptySlotException;
import it.polimi.ingsw.server.model.resources.Resource;

/**
 * This class is a couple (traducedPosition, ResourceContainer).
 */
public class TranslatedPosition {
    private final int shiftedPosition;
    private final ResourcesContainer container;

    public TranslatedPosition(Integer shiftedPosition, ResourcesContainer container) {
        this.shiftedPosition = shiftedPosition;
        this.container = container;
    }

    /**
     * Take and remove the resource stored into the slot of the translated position.
     *
     * @return the taken Resource
     * @throws EmptySlotException if in the translated position there is no Resource
     */
    public Resource takeResource() throws EmptySlotException {
        return container.takeResource(shiftedPosition);
    }

    /**
     * Add the resource toStock into the slot of the translated position.
     *
     * @param toStock Resource to stock
     * @return true if the Resource has been correctly stocked
     */
    public boolean setResource(Resource toStock) {
        return container.setResource(shiftedPosition, toStock);
    }

    /**
     * Get the resource stored in the slot of the translated position without removing it.
     *
     * @return a copy of the chosen Resource
     */
    public Resource getResource() {
        return container.getResource(shiftedPosition);
    }

}


package it.polimi.ingsw.server.model.player;

import it.polimi.ingsw.exceptions.EmptySlotException;
import it.polimi.ingsw.server.model.resources.Resource;

public class TranslatedPosition{
    private final int position;
    private final ResourcesContainer container;

    public TranslatedPosition(Integer position, ResourcesContainer container) {
        this.position = position;
        this.container = container;
    }

    /**
     * Take and remove the resource stored into the slot of the translated position.
     *
     * @return the taken Resource
     */
    public Resource takeResource() throws EmptySlotException {
        return container.takeResource(position);
    }

    /**
     * Add the resource toStock into the slot of the translated position.
     *
     * @param toStock           Resource to stock
     * @return true if the Resource has been correctly stocked
     */
    public boolean setResource(Resource toStock){
        return container.setResource(position, toStock);
    }

    /**
     * Get the resource stored in the slot of the translated position without removing it.
     *
     * @return a copy of the chosen Resource
     */
    public Resource getResource(){
        return container.getResource(position);
    }
}


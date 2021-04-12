package it.polimi.ingsw.server.model.player;

import it.polimi.ingsw.exceptions.EmptySlotException;
import it.polimi.ingsw.server.model.resources.Resource;

public class TranslatedPositionTuple<Integer, ResourceContainer> {
    private int position;
    private ResourcesContainer container;

    public TranslatedPositionTuple(Integer position, ResourcesContainer container) {
        this.position = (int) position;
        this.container = container;
    }

    public Resource getResource() throws EmptySlotException {
        return container.getResource(position);
    }

    public boolean setResource(Resource toStock){
        return container.setResource(position, toStock);
    }
}

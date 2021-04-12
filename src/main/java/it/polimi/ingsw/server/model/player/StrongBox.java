package it.polimi.ingsw.server.model.player;

import it.polimi.ingsw.exceptions.EmptySlotException;
import it.polimi.ingsw.server.model.resources.Resource;

import java.util.ArrayList;
import java.util.List;

public class StrongBox implements ResourcesContainer{
    List<Resource> slots = new ArrayList<>();

    /**
     * Store the resourceToStock in the StrongBox.
     *
     * @param producedResource to store
     * @return true if the resourceToStock has been correctly stored
     */
    public boolean addResource(Resource producedResource) {
        return this.slots.add(producedResource);
    }

    /**
     * Resource cannot be added to the StrongBox during a warehouse reordering.
     *
     * @param position of the chosen slot
     * @param toStock  Resource to stock
     * @return false
     */
    @Override
    public boolean setResource(int position, Resource toStock) {
        return false;
    }

    /**
     * Remove the resource stored into the slot defined by the given position.
     *
     * @param position of the chosen slot
     * @return the taken Resource
     */
    @Override
    public Resource getResource(int position) throws EmptySlotException {
        Resource chosenResource;
        try {
            chosenResource = this.slots.get(position);
        }catch (IndexOutOfBoundsException e){
            throw new EmptySlotException();
        }
        this.slots.set(position, null);
        return chosenResource;
    }
}

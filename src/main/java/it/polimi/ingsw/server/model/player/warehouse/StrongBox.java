package it.polimi.ingsw.server.model.player.warehouse;

import it.polimi.ingsw.server.exceptions.EmptySlotException;
import it.polimi.ingsw.server.model.resources.Resource;
import it.polimi.ingsw.server.model.resources.StorableResource;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * This class represent the StrongBox of the Player
 */
public class StrongBox implements ResourcesContainer {
    protected List<Resource> slots = new ArrayList<>();

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
     * Take and remove the resource stored into the slot defined by the given position.
     *
     * @param position of the chosen slot
     * @return the taken Resource
     * @throws EmptySlotException if the chosen slot is empty
     */
    @Override
    public Resource takeResource(int position) throws EmptySlotException {
        Resource chosenResource;
        try {
            chosenResource = this.slots.get(position);
        } catch (IndexOutOfBoundsException e) {
            throw new EmptySlotException("a non valid slot was selected");
        }
        if (chosenResource == null)
            throw new EmptySlotException("no resource in a selected slot");
        this.slots.set(position, null);
        return chosenResource;
    }

    /**
     * Get the resource stored into the slot defined by the given position without removing it from the StrongBox.
     *
     * @param position of the chosen slot
     * @return a copy of the chosen Resource if present, else return null
     */
    @Override
    public Resource getResource(int position) {
        Resource chosenResource;
        try {
            chosenResource = this.slots.get(position);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
        if (chosenResource != null)
            return new StorableResource(chosenResource.getColor());
        return null;
    }

    /**
     * Reorder the StrongBox removing empty slots.
     */
    protected void reorder() {
        slots = slots.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }
}

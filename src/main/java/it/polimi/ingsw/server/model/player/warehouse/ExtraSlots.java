package it.polimi.ingsw.server.model.player.warehouse;

import it.polimi.ingsw.server.model.resources.Resource;
import it.polimi.ingsw.server.model.resources.StorableResource;

import java.util.Arrays;
import java.util.List;

/**
 * Collection of slots of the same fixed type of resource and provided by a WarehouseLeaderCard.
 */
public class ExtraSlots extends Depot {

    /**
     * Number of extra slots
     */
    private final int capability = 2;

    private List<Resource> slots;

    private Resource resourceType;

    private boolean isActivated;

    public ExtraSlots() {
        this.isActivated = false;
    }

    /**
     * Method that return true if the Extra Slots are activated, false otherwise
     *
     * @return true if the Extra Slots are activated, false otherwise
     */
    public boolean isActivated() {
        return this.isActivated;
    }

    /**
     * Activate this extra slots
     *
     * @param typeOfResource that will contain this extra slots
     */
    public void activateExtraSlots(Resource typeOfResource) {
        this.isActivated = true;
        this.resourceType = typeOfResource;
        this.slots = Arrays.asList(new Resource[capability]);
    }

    /**
     * Add the resource toStock into the slot defined by the given position.
     *
     * @param position of the chosen slot (0 <= position <= slots.size())
     * @param toStock  Resource to stock
     * @return true if the Resource has been correctly stocked
     */
    @Override
    public boolean setResource(int position, Resource toStock) {
        this.slots.set(position, toStock);
        return true;
    }

    /**
     * Remove the resource stored into the slot defined by the given position.
     *
     * @param position of the chosen slot (0 <= position <= slots.size())
     * @return the taken Resource
     */
    @Override
    public Resource takeResource(int position) {
        Resource chosenResource = this.slots.get(position);
        setResource(position, null);
        return chosenResource;
    }

    @Override
    public Resource getResourceType() {
        return this.resourceType;
    }

    /**
     * Get the resource stored into the slot defined by the given position without removing it.
     *
     * @param position of the chosen slot (0 <= position <= slots.size())
     * @return a copy of the chosen Resource if present, else return null
     */
    @Override
    public Resource getResource(int position) {
        Resource chosenResource = this.slots.get(position);
        if (chosenResource != null)
            return new StorableResource(chosenResource.getColor());
        return null;
    }

    /**
     * Verify that each resource's type is the same as the fixed typeOfResources.
     *
     * @return true if each resource's type is the same as the fixed typeOfResources
     */
    @Override
    public boolean isLegal() {
        for (Resource extraResource : slots)
            if (extraResource != null && !extraResource.equals(resourceType))
                return false;
        return true;
    }
}
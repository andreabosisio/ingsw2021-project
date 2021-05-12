package it.polimi.ingsw.server.model.player.warehouse;

import it.polimi.ingsw.server.model.resources.StorableResource;
import it.polimi.ingsw.server.model.resources.Resource;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Collection of slots of the same type of resource
 */
public class Depot implements ResourcesContainer {

    private int numberOfSlots;

    private Resource resourceType;

    private List<Resource> slots;

    public Depot(int numberOfSlots) {
        this.numberOfSlots = numberOfSlots;
        this.slots = Arrays.asList(new Resource[numberOfSlots]);
    }

    public Depot() {
    }

    public Resource getResourceType() {
        return resourceType;
    }

    /**
     * Verify that all the resources of the depot are of the same type.
     *
     * @return true if all the resources of the depot are of the same type
     */
    public boolean isLegal(){
        return slots.stream().filter(Objects::nonNull).distinct().count() <= 1;
    }

    /**
     * Update the typeOfResource of this Depot after swap.
     */
    private void updateTypeResource(){
        for(Resource resource : slots)
            if(resource != null) {
                this.resourceType = resource;
                return;
            }
        resourceType = null;
    }

    /**
     * Add the resource toStock into the slot defined by the given position
     * and update the typeOfResource of this Depot.
     *
     * @param position of the chosen slot (0 <= position <= slots.size())
     * @param toStock  Resource to stock
     * @return true if the Resource has been correctly stocked
     */
    @Override
    public boolean setResource(int position, Resource toStock) {
        this.slots.set(position, toStock);
        updateTypeResource();
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

    /**
     * Get the resource stored into the slot defined by the given position without removing it.
     *
     * @param position of the chosen slot (0 <= position <= slots.size())
     * @return a copy of the chosen Resource if present, else return null
     */
    @Override
    public Resource getResource(int position) {
        Resource chosenResource = this.slots.get(position);
        if(chosenResource != null)
            return new StorableResource(chosenResource.getColor());
        return null;
    }
}

package it.polimi.ingsw.server.model.player;

import it.polimi.ingsw.server.model.resources.Resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Collection of slots of the same type of resource
 */
public class Depot implements ResourcesContainer{

    int numberOfSlots;

    Resource typeOfResources;

    List<Resource> slots;

    public Depot(int numberOfSlots) {
        this.numberOfSlots = numberOfSlots;
        this.slots = Arrays.asList(new Resource[numberOfSlots]);
    }

    public Depot() {
    }

    public Resource getTypeOfResources() {
        return typeOfResources;
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
                typeOfResources = resource;
                return;
            }
        typeOfResources = null;
    }

    /**
     * Add the resource toStock into the slot defined by the given position
     * and update the typeOfResource of this Depot.
     *
     * @param position of the chosen slot
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
     * @param position of the chosen slot
     * @return the taken Resource
     */
    @Override
    public Resource getResource(int position) {
        Resource chosenResource = this.slots.get(position);
        setResource(position, null);
        return chosenResource;
    }
}
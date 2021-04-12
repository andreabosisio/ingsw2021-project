package it.polimi.ingsw.server.model.player;

import it.polimi.ingsw.server.model.resources.Resource;

import java.util.Arrays;
import java.util.List;

/**
 * Collection of slots of the same fixed type of resource and provided by a WarehouseLeaderCard.
 */
public class ExtraSlots extends Depot{

    /**
     * Number of extra slots
     */
    private static final int capability = 2;

    private boolean isActivated;

    public ExtraSlots(){
        this.isActivated = false;
    }

    public boolean getIsActivated(){
        return isActivated;
    }

    /**
     * Activate this extra slots
     *
     * @param typeOfResource that will contain this extra slots
     */
    public void activateExtraSlots(Resource typeOfResource){
        this.isActivated = true;
        this.typeOfResources = typeOfResource;
        this.slots = Arrays.asList(new Resource[capability]);
    }

    /**
     * Add the resource toStock into the slot defined by the given position.
     *
     * @param position of the chosen slot
     * @param toStock           Resource to stock
     * @return true if the Resource has been correctly stocked
     */
    @Override
    public boolean setResource(int position, Resource toStock){
        this.slots.set(position, toStock);
        return true;
    }

    /**
     * Verify that each resource's type is the same as the fixed typeOfResources.
     *
     * @return true if each resource's type is the same as the fixed typeOfResources
     */
    @Override
    public boolean isLegal() {
        for (Resource extraResource : slots)
            if(extraResource != null && !extraResource.equals(typeOfResources))
                return false;
        return true;
    }
}

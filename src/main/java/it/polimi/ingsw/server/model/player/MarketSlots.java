package it.polimi.ingsw.server.model.player;

import it.polimi.ingsw.server.model.resources.OtherResource;
import it.polimi.ingsw.server.model.resources.Resource;

import java.util.Arrays;
import java.util.List;

public class MarketSlots implements ResourcesContainer{
    final int availableResourcesFromMarketSlots = 4;
    private final List<Resource> slots;

    public MarketSlots() {
        this.slots = Arrays.asList(new Resource[availableResourcesFromMarketSlots]);
    }

    /**
     * Add the resources taken from the MarketTray to this container
     * @param newResources taken from the MarketTray
     * @return true
     */
    public boolean addResources(List<Resource> newResources){
        int i = 0;
        for(Resource newResource : newResources) {
            this.slots.set(i, newResource);
            i++;
        }
        while (i < availableResourcesFromMarketSlots) {
            this.slots.set(i, null);
            i++;
        }
        return true;
    }

    /**
     * Report how many new resources taken from the MarketTray has not been stored in the depots by the player.
     *
     * @return the number of the resources in this container
     */
    public int getNumberOfRemainedResources(){
        int remained = 0;
        for (int i = 0; i < availableResourcesFromMarketSlots; i++)
            if(slots.get(i) != null) {
                remained++;
                slots.set(i, null);
            }
        return remained;
    }

    /**
     * Add the resource toStock into the slot defined by the given position.
     *
     * @param position of the chosen slot
     * @param toStock           Resource to stock
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
     * @param position of the chosen slot
     * @return the taken Resource
     */
    @Override
    public Resource takeResource(int position) {
        Resource chosenResource = this.slots.get(position);
        this.slots.set(position, null);
        return chosenResource;
    }

    /**
     * Cannot take Resources from the MarketSlotsZone.
     *
     * @param position of the chosen slot
     * @return null
     */
    @Override
    public Resource getResource(int position) {
        return null;
    }
}
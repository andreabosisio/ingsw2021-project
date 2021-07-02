package it.polimi.ingsw.server.model.player.warehouse;

import it.polimi.ingsw.server.model.resources.Resource;

import java.util.Arrays;
import java.util.List;

/**
 * This class represents the Slots that contains the Resource from Market
 */
public class MarketSlots implements ResourcesContainer {
    final int availableResourcesFromMarketSlots = 4;
    private final List<Resource> slots;
    private int lastIndex = 0;

    public MarketSlots() {
        this.slots = Arrays.asList(new Resource[availableResourcesFromMarketSlots]);
    }

    /**
     * Add the resources taken from the MarketTray to this container.
     *
     * @param newResources taken from the MarketTray (newResources.size() <= availableResourcesFromMarketSlots)
     * @return true
     */
    public boolean addResources(List<Resource> newResources) {
        for (Resource newResource : newResources) {
            try {
                this.slots.set(lastIndex, newResource);
                lastIndex++;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    /**
     * Report how many new resources taken from the MarketTray has not been stored in the depots by the player
     * and resets the resources from the MarketTray.
     *
     * @return the number of the resources in this container
     */
    public int getNumberOfRemainingResources() {
        int remained = 0;
        for (int i = 0; i < availableResourcesFromMarketSlots; i++)
            if (slots.get(i) != null) {
                remained++;
                slots.set(i, null);
            }
        this.lastIndex = 0;
        return remained;
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
        this.slots.set(position, null);
        return chosenResource;
    }

    /**
     * Get the resource stored into the slot defined by the given position without removing it from the Warehouse.
     *
     * @param position of the chosen slot
     * @return a copy of the chosen Resource
     */
    @Override
    public Resource getResource(int position) {
        return this.slots.get(position);
    }

    /**
     * Get method that
     *
     * @return all the slots
     */
    public List<Resource> getSlots() {
        return slots;
    }
}
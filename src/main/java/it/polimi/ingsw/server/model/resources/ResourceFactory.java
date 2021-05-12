package it.polimi.ingsw.server.model.resources;

import it.polimi.ingsw.exceptions.NonStorableResourceException;
import it.polimi.ingsw.server.model.enums.ResourceEnum;

/**
 * Class used to have the access to produce StorableResource(s).
 */
public class ResourceFactory {
    /**
     * Produce a new StorableResource based on the given color.
     *
     * @param color of the resource to be produced
     * @return the StorableResource just produced
     * @throws NonStorableResourceException if the given color represents a NonStorableResource
     */
    public Resource produceResource(ResourceEnum color) throws NonStorableResourceException {

        if (NonStorableResources.getNonStorableResourcesEnum().contains(color))
            throw new NonStorableResourceException("Cannot produce a " + color.toString() + " resource because it's a NonStorableResource");
        else if (color.equals(ResourceEnum.EMPTY_RES))
            throw new NonStorableResourceException("Cannot produce an empty resource");

        return new StorableResource(color);
    }
}

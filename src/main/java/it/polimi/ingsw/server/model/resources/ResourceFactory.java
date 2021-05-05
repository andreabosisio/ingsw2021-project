package it.polimi.ingsw.server.model.resources;

import it.polimi.ingsw.exceptions.NonStorableResourceException;
import it.polimi.ingsw.server.model.enums.ResourceEnum;

public class ResourceFactory {
    public Resource produceResource(ResourceEnum color) throws NonStorableResourceException {

        if(NonStorableResources.getNonStorableResourcesEnum().contains(color))
            throw new NonStorableResourceException("Cannot produce a " + color.toString() + " resource because it's a NonStorableResource");

        return new StorableResource(color);
    }
}

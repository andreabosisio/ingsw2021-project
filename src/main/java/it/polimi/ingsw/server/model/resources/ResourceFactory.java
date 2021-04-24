package it.polimi.ingsw.server.model.resources;

import it.polimi.ingsw.exceptions.NonStorableResourceException;
import it.polimi.ingsw.server.model.enums.ResourceEnum;

public class ResourceFactory {
    public Resource produceResource(ResourceEnum color) throws NonStorableResourceException {

        if(NonStorableResources.getNonStorableResourcesEnum().contains(color))
            throw new NonStorableResourceException();

        return new StorableResource(color);
    }
}

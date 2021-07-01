package it.polimi.ingsw.server.model.resources;

import it.polimi.ingsw.commons.enums.NonStorableResourcesEnum;
import it.polimi.ingsw.commons.enums.ResourcesEnum;
import it.polimi.ingsw.server.exceptions.NonStorableResourceException;

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
    public static Resource produceResource(ResourcesEnum color) throws NonStorableResourceException {
        if (NonStorableResourcesEnum.getAsList().contains(color))
            throw new NonStorableResourceException("Cannot produce a " + color.toString() + " resource because it's a NonStorableResource");
        else if (color.equals(ResourcesEnum.EMPTY_RES))
            throw new NonStorableResourceException("Cannot produce an empty resource");

        return new StorableResource(color);
    }

    /**
     * Produce a new Resource to fill initial game's parts.
     * .
     *
     * @param color The color of the desired Resource
     * @return The new Resource
     */
    public static Resource produceInitialResource(String color) {
        try {
            return produceResource(ResourcesEnum.valueOf(color));
        } catch (NonStorableResourceException e) {
            if (color.equals(ResourcesEnum.WHITE.toString()))
                return new WhiteResource();
            else if (color.equals(ResourcesEnum.RED.toString()))
                return new RedResource();
        } catch (RuntimeException runtimeException) {
            runtimeException.printStackTrace();
        }
        return null;
    }
}

package it.polimi.ingsw.server.model.resources;

import it.polimi.ingsw.exceptions.NonStorableResourceException;
import it.polimi.ingsw.server.model.enums.ResourceEnum;
import it.polimi.ingsw.server.model.turn.TurnLogic;

import java.util.Objects;

/**
 * Abstract class for resources
 */
public abstract class Resource {

    private ResourceEnum color;

    public Resource(ResourceEnum color){
        this.color = color;
    }

    /**
     * Getter of the resource's color.
     *
     * @return the color of this Resource
     */
    public ResourceEnum getColor(){
        return this.color;
    }

    /**
     * Method to call after a Resource has been chosen in the MarketTray.
     *
     * @param turn  containing the current player, the current state of the game and others information
     * @return true if the ability has been executed successfully
     */
    public boolean marketAbility(TurnLogic turn){
        return false;
    }

    /**
     * Method to call after a Resource has been produced by a ProductionAction of a ProductionCard.
     *
     * @param turn  containing the current player, the current state of the game and others information
     * @return true if the ability has been executed successfully
     */
    public boolean productionAbility(TurnLogic turn) {
        return false;
    }

    /**
     * Verify if an Object is equals to this Resource based on the color of the resources.
     *
     * @param o an Object
     * @return true if Object is a Resource with the same color as the color of this Resource.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Resource))
            return false;
        Resource resource = (Resource) o;
        return color == resource.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color);
    }

    @Override
    public String toString() {
        return "Resource{" +
                "color=" + color +
                '}';
    }
    public boolean addPossibleTransformation(Resource possibleTransformation) {
        return false;
    }
}

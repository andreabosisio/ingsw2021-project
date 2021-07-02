package it.polimi.ingsw.server.model.resources;

import it.polimi.ingsw.commons.enums.ResourcesEnum;
import it.polimi.ingsw.server.model.turn.TurnLogic;

import java.util.Objects;


public abstract class Resource {

    private final ResourcesEnum color;

    public Resource(ResourcesEnum color) {
        this.color = color;
    }

    /**
     * Getter of the resource's color.
     *
     * @return the color of this Resource
     */
    public ResourcesEnum getColor() {
        return this.color;
    }

    /**
     * Method to call after a Resource has been chosen in the MarketTray.
     * Never used here
     *
     * @param turn containing the current player, the current state of the game and others information
     * @return true if the ability has been executed successfully
     */
    public abstract boolean marketAbility(TurnLogic turn);

    /**
     * Method to call after a Resource has been produced by a ProductionAction of a ProductionCard.
     *
     * @param turn containing the current player, the current state of the game and others information
     * @return true if the ability has been executed successfully
     */
    public boolean productionAbility(TurnLogic turn) {
        return false;
    }


    /**
     * Add the possible resource this resource can transform into
     *
     * @param possibleTransformation Resource in which this resource will be able to transform
     * @return false
     */
    public boolean addPossibleTransformation(Resource possibleTransformation) {
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
}

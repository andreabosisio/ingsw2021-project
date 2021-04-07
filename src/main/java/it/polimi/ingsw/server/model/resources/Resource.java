package it.polimi.ingsw.server.model.resources;

import it.polimi.ingsw.server.model.enums.ResourceEnum;
import it.polimi.ingsw.server.model.turn.TurnLogic;

import java.util.Objects;

public abstract class Resource {
    private ResourceEnum color;
    public Resource(ResourceEnum color){
        this.color = color;
    }

    public ResourceEnum getColor(){
        return this.color;
    }

    public boolean marketAbility(TurnLogic turn){
        return false;
    }

    public boolean productionAbility(TurnLogic turn){
        return false;
    }

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

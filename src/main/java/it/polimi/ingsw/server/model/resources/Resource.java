package it.polimi.ingsw.server.model.resources;

import it.polimi.ingsw.server.model.enums.ResourceEnum;
import it.polimi.ingsw.server.model.turn.TurnLogic;

public abstract class Resource {
    private ResourceEnum color;
    public Resource(ResourceEnum color){
        this.color = color;
    }
    public boolean ability(TurnLogic turn){
        return false;
    }
}

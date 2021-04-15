package it.polimi.ingsw.server.model.turn;

import it.polimi.ingsw.exceptions.InvalidEventException;
import it.polimi.ingsw.exceptions.NonStorableResourceException;
import it.polimi.ingsw.server.model.enums.ResourceEnum;
import it.polimi.ingsw.server.model.resources.NonStorableResources;
import it.polimi.ingsw.server.model.resources.OtherResource;
import it.polimi.ingsw.server.model.resources.Resource;

import java.util.List;

public class WaitTransformation extends State {
    public WaitTransformation(TurnLogic turnLogic) {
        super(turnLogic);
    }

    @Override
    public boolean transformationAction(List<String> chosenColors) throws InvalidEventException {
        if(chosenColors.size()!=turnLogic.getWhiteResourcesFromMarket().size()){
            throw new InvalidEventException();
        }
        for(String chosenColor:chosenColors) {
            try {
                if(NonStorableResources.getNonStorableResourcesEnum().contains(ResourceEnum.valueOf(chosenColor.toUpperCase()))){
                    throw new InvalidEventException();
                }

            } catch (IllegalArgumentException e) {
                throw new InvalidEventException();
            }
            //todo warehouse add
            //Resource r = new OtherResource(ResourceEnum.valueOf(chosenColor.toUpperCase()));
        }
        return false;
    }
}

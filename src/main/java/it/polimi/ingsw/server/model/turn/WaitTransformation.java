package it.polimi.ingsw.server.model.turn;

import it.polimi.ingsw.exceptions.InvalidEventException;
import it.polimi.ingsw.exceptions.NonStorableResourceException;
import it.polimi.ingsw.server.model.enums.ResourceEnum;
import it.polimi.ingsw.server.model.resources.NonStorableResources;
import it.polimi.ingsw.server.model.resources.OtherResource;
import it.polimi.ingsw.server.model.resources.Resource;

import java.util.ArrayList;
import java.util.List;

public class WaitTransformation extends State {
    public WaitTransformation(TurnLogic turnLogic) {
        super(turnLogic);
    }

    /**
     * Add the chosen resources for the white resource transformation to the warehouse's market zone
     * and set the current state of the game to WaitResourcePlacement.
     *
     * @param chosenColors of the chosen resources
     * @return true if the chosen resources has been correctly created
     * @throws InvalidEventException if one of the chosen resource type doesn't exists
     * @throws NonStorableResourceException if one of the chosen resource is a NonStorableResource
     */
    @Override
    public boolean transformationAction(List<String> chosenColors) throws InvalidEventException, NonStorableResourceException {
        if(chosenColors.size() != turnLogic.getWhiteResourcesFromMarket().size()){
            throw new InvalidEventException(); //wrong number of chosen resources
        }
        List<Resource> chosenResources = new ArrayList<>();
        for(String chosenColor : chosenColors) {
            try {
                ResourceEnum chosenEnum = ResourceEnum.valueOf(chosenColor.toUpperCase());
                if(NonStorableResources.getNonStorableResourcesEnum().contains(chosenEnum)){
                    throw new NonStorableResourceException(); //invalid type of chosen resources (RED, WHITE)
                }
                chosenResources.add(new OtherResource(chosenEnum));
            } catch (IllegalArgumentException e) {
                throw new InvalidEventException(); //non existing resource type
            }
        }
        //add the chosen resources to the warehouse market zone
        turnLogic.getCurrentPlayer().getPersonalBoard().getWarehouse().addResourcesFromMarket(chosenResources);
        turnLogic.setCurrentState(turnLogic.getWaitResourcePlacement());
        return true;
    }
}

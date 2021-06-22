package it.polimi.ingsw.server.model.cards;

import it.polimi.ingsw.server.model.resources.*;
import it.polimi.ingsw.server.model.turn.TurnLogic;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * This class is the implementation of the Basic Power Card.
 */
public class BasicPowerCard implements ProductionCard {

    private final int inResourceSlots = 2;
    private final int outResourceSlots = 1;
    private final int choosableInResourcesSlots = 2;
    private final int choosableOutResourcesSlots = 1;

    private final List<Resource> outResources = new ArrayList<>();

    /**
     * Setter of the desired outResources to be produced by the production of the card.
     *
     * @param desiredResources      list of the desired Resources
     * @return true if the outResources has been set correctly
     */
    public boolean setOutResources(List<Resource> desiredResources) {
        return outResources.addAll(desiredResources.subList(inResourceSlots, inResourceSlots + choosableOutResourcesSlots));
    }

    /**
     * Produce the desiredResource saved in outResources.
     *
     * @return true if the production has been applied correctly
     * @param turnLogic turn
     */
    @Override
    public boolean usePower(TurnLogic turnLogic) {
        for(Resource outResource : outResources)
            if(!outResource.productionAbility(turnLogic))
                return false;
        return true;
    }

    /**
     * Getter of the Resources required to activate the production of the Card.
     *
     * @return a new list of the Resources required to activate the production of the card
     */
    @Override
    public List<Resource> getInResources() {
        return null;
    }

    /**
     * Getter of the Resources provided by the production of the Card.
     *
     * @return a new list of the Resources provided by the production of the Card
     */
    @Override
    public List<Resource> getOutResources() {
        return new ArrayList<>(this.outResources);
    }

    /**
     * Check if the desiredProductionResources can activate the production of the Card.
     * If yes set the outResources to be produced.
     *
     * @param givenResources from a player
     * @return true if the desiredProductionResources can activate the production of the Card
     */
    @Override
    public boolean canDoProduction(List<Resource> givenResources) {
        this.outResources.clear();
        givenResources = givenResources.stream().filter(Objects::nonNull).collect(Collectors.toList());
        if(givenResources.size() != choosableInResourcesSlots + choosableOutResourcesSlots)
            return false;
        return setOutResources(givenResources);
    }

    /**
     * Return card's victory points.
     *
     * @return 0 points as default
     */
    @Override
    public int getPoints() {
        return 0;
    }

    /**
     * Return card's ID
     *
     * @return ID of the card
     */
    @Override
    public String getID() {
        return "basicPowerCard";
    }
}

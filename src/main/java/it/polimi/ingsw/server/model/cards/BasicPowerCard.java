package it.polimi.ingsw.server.model.cards;

import it.polimi.ingsw.exceptions.NonStorableResourceException;
import it.polimi.ingsw.server.model.enums.ResourceEnum;
import it.polimi.ingsw.server.model.resources.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class BasicPowerCard implements ProductionCard {

    private final int inResourceSlots = 2;
    private final int outResourceSlots = 1;
    private final int choosableInResourcesSlots = 2;
    private final int choosableOutResourcesSlots = 1;

    private List<Resource> outResources = new ArrayList<>();

    /**
     * Setter of the desired outResources to be produced by the production of the card.
     * @param desiredResources      list of the desired Resources
     * @return true if the outResources has been set correctly
     */
    public boolean setOutResources(List<Resource> desiredResources) {
        return outResources.addAll(desiredResources.subList(inResourceSlots, inResourceSlots + choosableOutResourcesSlots));
    }

    /**
     * Produce the desiredResource saved in outResources.
     * @return a list with new resources chosen with setOutResources
     */
    @Override
    public List<Resource> usePower() {
        List<Resource> producedResources;
        producedResources = this.outResources.stream()
                .map(r -> new OtherResource(r.getColor()))
                .collect(Collectors.toList());
        return producedResources;
    }

    /**
     * Getter of the Resources required to activate the production of the card.
     * @return a new list of the Resources required to activate the production of the card
     */
    @Override
    public List<Resource> getInResources() {
        return null;
    }

    /**
     * Getter of the Resources provided by the production of the Card.
     * @return a new list of the Resources provided by the production of the card
     */
    @Override
    public List<Resource> getOutResources() {
        return new ArrayList<>(this.outResources);
    }

    /**
     * Check if the desiredProductionResources can activate the production of the card.
     * If yes set the outResources to be produced.
     *
     * @param desiredProductionResources from a player
     * @return true if the desiredProductionResources can activate the production of the card
     * @throws NonStorableResourceException if desiredProductionResources contains a RedResource or a WhiteResource
     */
    @Override
    public boolean canDoProduction(List<Resource> desiredProductionResources) throws NonStorableResourceException {
        if(desiredProductionResources.size() != choosableInResourcesSlots + choosableOutResourcesSlots)
            return false;
        if(!Collections.disjoint(desiredProductionResources, NonStorableResources.getNonStorableResources()))
            throw new NonStorableResourceException();
        return setOutResources(desiredProductionResources);
    }
}

package it.polimi.ingsw.server.model.cards;

import it.polimi.ingsw.exceptions.NonStorableResourceException;
import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.server.model.resources.*;
import it.polimi.ingsw.server.model.turn.TurnLogic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProductionLeaderCard extends LeaderCard implements ProductionCard {

    private final int inResourceSlots = 1;
    private final int outResourceSlots = 2;
    private final int choosableInResourcesSlots = 0;
    private final int choosableOutResourcesSlots = 1;

    private final Resource inResources;
    private List<Resource> outResources = new ArrayList<>();

    /**
     * Redefined constructor.
     *
     * Return a new LeaderCardProduction with a RedResource in outResource and set correctly all the parameters.
     * @param ID of the LeaderCard
     * @param points        victory points given by the card
     * @param requirements to activate the LeaderCard
     * @param inResource required to do the production
     */
    public ProductionLeaderCard(String ID, int points, List<Requirement> requirements, Resource inResource) {
        super(ID, points, requirements);
        this.inResources = inResource;
        this.outResources.add(new RedResource());
    }

    /**
     * Setter of the desired outResources to be produced by the production of the card.
     *
     * @param desiredResources      list of the desired Resources
     * @return true if the outResources has been set correctly
     */
    private boolean setOutResources(List<Resource> desiredResources) {
        return outResources.addAll(desiredResources);
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
        this.outResources.clear();
        return this.outResources.add(new RedResource());
    }

    /**
     * Getter of the Resource required to activate the production of the card.
     *
     * @return a new list of the Resources required to activate the production of the card
     */
    @Override
    public List<Resource> getInResources() {
        List<Resource> inResourcesCopy = new ArrayList<>();
        inResourcesCopy.add(inResources);
        return inResourcesCopy;
    }

    /**
     * Getter of the Resources provided by the production of the card.
     *
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
        if(desiredProductionResources.size() != inResourceSlots + choosableOutResourcesSlots)
            return false;
        if(!Collections.disjoint(desiredProductionResources, NonStorableResources.getNonStorableResources()))
            throw new NonStorableResourceException();
        if(!desiredProductionResources.get(0).equals(this.inResources))
            return false;

        return setOutResources(desiredProductionResources.subList(inResourceSlots, choosableOutResourcesSlots + 1));
    }

    /**
     * Activate the LeaderCard for the player and (only after) add it to personalBoard list of active leaders
     *
     * @param player player owner of the card
     * @return true if activated successfully
     */
    @Override
    public boolean activate(Player player) {
        player.getPersonalBoard().setNewDevCard(this);
        return super.activate(player);
    }
}

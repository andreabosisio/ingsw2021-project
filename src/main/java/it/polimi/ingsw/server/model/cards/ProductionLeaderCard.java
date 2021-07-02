package it.polimi.ingsw.server.model.cards;

import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.server.model.resources.RedResource;
import it.polimi.ingsw.server.model.resources.Resource;
import it.polimi.ingsw.server.model.turn.TurnLogic;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Implements the Leader Card with the ability to do a Production
 */
public class ProductionLeaderCard extends LeaderCard implements ProductionCard {

    private static final int IN_RESOURCE_SLOTS = 1;
    private static final int OUT_RESOURCE_SLOTS = 2;
    private static final int CHOOSABLE_IN_RESOURCES_SLOTS = 0;
    private static final int CHOOSABLE_OUT_RESOURCES_SLOTS = 1;

    private final Resource inResources;
    private final List<Resource> outResources = new ArrayList<>();

    /**
     * Redefined constructor.
     * <p>
     * Return a new LeaderCardProduction with a RedResource in outResource and set correctly all the parameters.
     *
     * @param ID           of the LeaderCard
     * @param points       victory points given by the card
     * @param requirements to activate the LeaderCard
     * @param inResource   required to do the production
     */
    public ProductionLeaderCard(String ID, int points, List<Requirement> requirements, Resource inResource) {
        super(ID, points, requirements);
        this.inResources = inResource;
        this.outResources.add(new RedResource());
    }

    /**
     * Setter of the desired outResources to be produced by the production of the card.
     *
     * @param desiredResources list of the desired Resources
     * @return true if the outResources has been set correctly
     */
    private boolean setOutResources(List<Resource> desiredResources) {
        return outResources.addAll(desiredResources);
    }

    /**
     * Produce the desiredResource saved in outResources.
     *
     * @param turnLogic turn
     * @return true if the production has been applied correctly
     */
    @Override
    public boolean usePower(TurnLogic turnLogic) {
        this.outResources.add(new RedResource());
        for (Resource outResource : outResources)
            if (!outResource.productionAbility(turnLogic))
                return false;
        return true;
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
     */
    @Override
    public boolean canDoProduction(List<Resource> desiredProductionResources) {

        this.outResources.clear();

        desiredProductionResources = desiredProductionResources.stream().filter(Objects::nonNull).collect(Collectors.toList());

        if (desiredProductionResources.size() != IN_RESOURCE_SLOTS + CHOOSABLE_OUT_RESOURCES_SLOTS)
            return false;

        if (!desiredProductionResources.get(0).equals(this.inResources))
            return false;

        return setOutResources(desiredProductionResources.subList(IN_RESOURCE_SLOTS, CHOOSABLE_OUT_RESOURCES_SLOTS + 1));
    }

    /**
     * Activate the LeaderCard for the player and (only after) add it to personalBoard list of active leaders
     *
     * @param player player owner of the card
     * @return true if activated successfully
     */
    @Override
    public boolean activate(Player player) {
        player.getPersonalBoard().setNewProductionCard(this);
        return super.activate(player);
    }
}

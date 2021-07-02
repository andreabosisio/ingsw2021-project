package it.polimi.ingsw.server.model.cards;

import it.polimi.ingsw.server.model.resources.Resource;
import it.polimi.ingsw.server.model.turn.TurnLogic;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * This class is the implementation of the Basic Power Card.
 */
public class BasicPowerCard implements ProductionCard {

    private static final int IN_RESOURCE_SLOTS = 2;
    private static final int OUT_RESOURCE_SLOTS = 1;
    private static final int CHOOSABLE_IN_RESOURCES_SLOTS = 2;
    private static final int CHOOSABLE_OUT_RESOURCES_SLOTS = 1;

    private final List<Resource> outResources = new ArrayList<>();

    /**
     * Setter of the desired outResources to be produced by the production of the card.
     *
     * @param desiredResources list of the desired Resources
     * @return true if the outResources has been set correctly
     */
    public boolean setOutResources(List<Resource> desiredResources) {
        return outResources.addAll(desiredResources.subList(IN_RESOURCE_SLOTS, IN_RESOURCE_SLOTS + CHOOSABLE_OUT_RESOURCES_SLOTS));
    }

    /**
     * Produce the desiredResource saved in outResources.
     *
     * @param turnLogic turn
     * @return true if the production has been applied correctly
     */
    @Override
    public boolean usePower(TurnLogic turnLogic) {
        for (Resource outResource : outResources)
            if (!outResource.productionAbility(turnLogic))
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
        if (givenResources.size() != CHOOSABLE_IN_RESOURCES_SLOTS + CHOOSABLE_OUT_RESOURCES_SLOTS)
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

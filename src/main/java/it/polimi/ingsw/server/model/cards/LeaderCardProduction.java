package it.polimi.ingsw.server.model.cards;

import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.server.model.resources.RedResource;
import it.polimi.ingsw.server.model.resources.Resource;

import java.util.List;

public class LeaderCardProduction extends LeaderCard implements ProductionCard {
    private final Resource outResource = new RedResource();
    private final Resource inResource;


    public LeaderCardProduction(String ID, int points, List<Requirement> requirements,Resource inResource) {
        super(ID, points, requirements);
        this.inResource=inResource;
    }

    /**
     * Getter that returns the ID of the card.
     *
     * @return a String containing an ID
     */
    @Override
    public String getID() {
        return super.getID();
    }

    /**
     * Getter that returns the endgame points of the card.
     * @return an int for the points
     */
    @Override
    public int getPoints() {
        return super.getPoints();
    }

    /**
     * @param resource
     */

    @Override
    public boolean doTransformation(Resource resource) {
        return super.doTransformation(resource);
    }

    /**
     * Add the discount this card provide to currDiscounts
     *
     * @param currDiscounts list where this card discount will be added
     * @return true if discount was added successfully
     */
    @Override
    public boolean applyDiscount(List<Resource> currDiscounts) {
        return super.applyDiscount(currDiscounts);
    }

    /**
     * Check if the LeaderCard can be activated by the player
     *
     * @param player player activating the card
     * @return true if it can be activated
     */
    @Override
    public boolean canBeActivated(Player player) {
        return super.canBeActivated(player);
    }

    @Override
    public List<Resource> usePower() {
        return null;
    }

    @Override
    public List<Resource> getInResources() {
        return null;
    }

    @Override
    public List<Resource> getOutResources() {
        return null;
    }

    @Override
    public boolean canDoProduction(List<Resource> resources) {
        return false;
    }

    private boolean activate(Player player){
        return false;
    }
}

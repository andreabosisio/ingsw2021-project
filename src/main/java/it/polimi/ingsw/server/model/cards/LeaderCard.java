package it.polimi.ingsw.server.model.cards;

import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.server.model.resources.Resource;

import java.util.List;

public abstract class LeaderCard {
    private final List<Requirement> requirements;
    private final String ID;
    private final int points;

    LeaderCard(String ID,int points,List<Requirement> requirements){
        this.ID=ID;
        this.points=points;
        this.requirements=requirements;
    }

    /**
     * Getter that returns the ID of the card.
     *
     * @return a String containing an ID
     */
    public String getID() {
        return ID;
    }

    /**
     * Getter that returns the endgame points of the card.
     *
     * @return an int for the points
     */
    public int getPoints() {
        return points;
    }

    /**
     *Check if the white resource transformation can be performed by the card.
     *
     * @return true if it is acceptable
     */
    public boolean doTransformation(Resource resource) {
        return false;
    }

    /**
     * Add the discount this card provide to currDiscounts.
     *
     * @param currentDiscounts list where this card discount will be added
     * @return true if discount was added successfully
     */
    public boolean applyDiscount(List<Resource> currentDiscounts){
        return false;
    }

    /**
     * Check if the LeaderCard can be activated by the player.
     *
     * @param player player activating the card
     * @return true if it can be activated
     */
    public boolean canBeActivated(Player player){
        for(Requirement requirement : requirements){
            if(!requirement.isSatisfied(player)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Activate the LeaderCard for the player and (only after) add it to personalBoard list of active leaders.
     *
     * @param player player owner of the card
     * @return true if activated successfully
     */
    public boolean activate(Player player){
        return player.getPersonalBoard().addToActiveLeaders(this);
    }
}
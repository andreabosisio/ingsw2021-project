package it.polimi.ingsw.server.model.cards;

import it.polimi.ingsw.commons.enums.CardColorsEnum;
import it.polimi.ingsw.server.model.player.Player;

import java.util.List;

/**
 * Represents the requirements to Activate a Leader Card in terms of owned Development Cards.
 */
public class DevelopmentRequirement implements Requirement {
    private final int level;
    private final CardColorsEnum color;
    private final int quantity;

    /**
     * Create a new Development Requirement by setting the needed parameters.
     *
     * @param level Minimum level of the owned Development Cards
     * @param color Color of the owned Development Cards
     * @param quantity Quantity of the owned Development Cards that must respect this Requirement
     */
    public DevelopmentRequirement(int level, CardColorsEnum color, int quantity) {
        this.level = level;
        this.color = color;
        this.quantity = quantity;
    }

    /**
     * Check if the condition is satisfied by the player
     *
     * @param player player to check
     * @return true if satisfied
     */
    @Override
    public boolean isSatisfied(Player player) {
        //lv=0 if level is not specified
        List<DevelopmentCard> cards = player.getPersonalBoard().getAllDevelopmentCards();
        if (level > 0) {
            return cards.stream().filter(card -> card.getColor() == color && card.getLevel() == level).count() >= quantity;
        } else
            return cards.stream().filter(card -> card.getColor() == color).count() >= quantity;
    }
}

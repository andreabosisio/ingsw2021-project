package it.polimi.ingsw.server.model.cards;

import it.polimi.ingsw.server.model.enums.CardColorEnum;
import it.polimi.ingsw.server.model.player.Player;

import java.util.List;

public class DevelopmentRequirement implements Requirement {
    private final int level;
    private final CardColorEnum color;
    private final int quantity;

    public DevelopmentRequirement(int level, CardColorEnum color, int quantity) {
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

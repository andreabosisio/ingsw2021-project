package it.polimi.ingsw.server.model.cards;

import it.polimi.ingsw.server.model.player.Player;

/**
 * Represents the needed requirements to activate a Leader Card.
 */
public interface Requirement {
    /**
     * Check if the condition to activate a Leader Card is satisfied by the Player.
     *
     * @param player player to check
     * @return true if satisfied
     */
    boolean isSatisfied(Player player);
}

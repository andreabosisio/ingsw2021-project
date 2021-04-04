package it.polimi.ingsw.server.model.cards;

import it.polimi.ingsw.server.model.player.Player;

public interface Requirement {
    /**
     * Check if the condition is satisfied by the player
     * @param player player to check
     * @return true if satisfied
     */
    boolean isSatisfied(Player player);
}

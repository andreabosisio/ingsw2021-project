package it.polimi.ingsw.server.events.send.graphics;

import it.polimi.ingsw.server.model.player.Player;

/**
 * Represent the updates containing the data to update the Client of a specific Player PersonalBoard changes.
 */
public interface PersonalUpdate {

    /**
     * Add a Personal Board Update of a specific Player.
     *
     * @param personalBoardUpdate A Personal Board Update
     * @param player The Player owner of the Personal Board with updates
     */
    void addUpdate(PersonalBoardUpdate personalBoardUpdate, Player player);
}

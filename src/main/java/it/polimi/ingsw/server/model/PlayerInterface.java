package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.player.PersonalBoard;

public interface PlayerInterface {

    /**
     * Getter for player personalBoard
     *
     * @return player's personalBoard
     */
    PersonalBoard getPersonalBoard();

    /**
     * Get method that
     *
     * @return the nickname of the Player
     */
    String getNickname();
}
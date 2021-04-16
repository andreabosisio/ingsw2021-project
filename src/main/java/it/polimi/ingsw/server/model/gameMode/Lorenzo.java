package it.polimi.ingsw.server.model.gameMode;

import it.polimi.ingsw.server.model.PlayerInterface;

/**
 * Interface that represents the general type Lorenzo
 */
public interface Lorenzo extends PlayerInterface {

    /**
     * This method do the Lorenzo's turn if it is called by the class LorenzoAI,
     * if it is called by the class LorenzoDoingNothing it does nothing.
     *
     * @return true if Lorenzo is playing in this game
     */
    boolean play();
}
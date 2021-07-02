package it.polimi.ingsw.server.model.gameMode;

import it.polimi.ingsw.server.model.PlayerInterface;
import it.polimi.ingsw.server.model.turn.TurnLogic;

/**
 * Interface that represents the general type Lorenzo
 */
public interface Lorenzo extends PlayerInterface {

    /**
     * This method do the Lorenzo's turn if it is called by the class LorenzoAI,
     * if it is called by the class LorenzoDoingNothing it does nothing.
     *
     * @param turnLogic is the TurnLogic reference
     * @return true if Lorenzo is playing in this game
     */
    boolean play(TurnLogic turnLogic);

    /**
     * Method used for testing.
     *
     * @return the extracted Solo Action Token
     */
    SoloActionToken extractToken();

    /**
     * load the solo action tokens saved in the appropriate file
     */
    void loadSavedTokens();

    /**
     * This method loads the tokens used in a normal Game
     */
    void generateNormalTokens();
}
package it.polimi.ingsw.server.model.gameMode;

import it.polimi.ingsw.server.model.turn.TurnLogic;

/**
 * Class that represents the token that increments of one position
 * the Black Cross Token in the Lorenzo's Faith Track and don't shuffle the deck of Solo Action Tokens
 */
public class SingleFaithTrackProgressTokenNoShuffle extends SingleFaithTrackProgressToken{
    /**
     * This method increments the position of the Black Cross Token of one position
     * and it return false to not shuffle the deck of Solo Action Tokens.
     *
     * @param lorenzo   is used to access the Lorenzo's Faith Track
     * @param turnLogic is the TurnLogic reference
     * @return false
     */
    @Override
    public boolean doAction(Lorenzo lorenzo, TurnLogic turnLogic) {
        super.doAction(lorenzo, turnLogic);
        return false;
    }
}

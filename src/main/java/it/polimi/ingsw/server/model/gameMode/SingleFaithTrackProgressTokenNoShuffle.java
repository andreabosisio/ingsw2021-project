package it.polimi.ingsw.server.model.gameMode;

import it.polimi.ingsw.server.model.turn.TurnLogic;

public class SingleFaithTrackProgressTokenNoShuffle extends SingleFaithTrackProgressToken{
    /**
     * This method increments the position of the Black Cross Token of one position.
     * This is the only do Action method that return true,
     * in fact Lorenzo must shuffle the deck of Solo Action Tokens.
     *
     * @param lorenzo   is used to access the Lorenzo's Faith Track
     * @param turnLogic is the TurnLogic reference
     * @return true
     */
    @Override
    public boolean doAction(Lorenzo lorenzo, TurnLogic turnLogic) {
        super.doAction(lorenzo, turnLogic);
        return false;
    }
}

package it.polimi.ingsw.server.model.gameMode;

import it.polimi.ingsw.server.model.turn.TurnLogic;

/**
 * Interface that represents the general type Token
 */
public abstract class SoloActionToken {
    private final String type;

    protected SoloActionToken(String type) {
        this.type = type;
    }

    /**
     * Method that implements the action of the token
     *
     * @param lorenzo   is used to access the Lorenzo's Faith Track
     * @param turnLogic is the TurnLogic reference
     * @return true if the action is did by the class SingleFaithTrackProgress
     */
    public abstract boolean doAction(Lorenzo lorenzo, TurnLogic turnLogic);
}
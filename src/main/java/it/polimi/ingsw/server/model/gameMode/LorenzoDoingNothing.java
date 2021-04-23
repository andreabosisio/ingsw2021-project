package it.polimi.ingsw.server.model.gameMode;

import it.polimi.ingsw.server.model.player.PersonalBoard;

/**
 * This class is set by the class GameMode when there is a MultiPlayers Game.
 */
public class LorenzoDoingNothing implements Lorenzo{
    /**
     * This method do nothing
     *
     * @return false
     */
    @Override
    public boolean play() {
        return false;
    }

    /**
     * Get method that
     *
     * @return the nickname of the Player
     */
    @Override
    public String getNickname() {
        return "Lorenzo il Magnifico doing nothing";
    }

    @Override
    public PersonalBoard getPersonalBoard() {
        return null;
    }

    /**
     * Method used for testing
     *
     * @return the Solo Action Token extract
     */
    @Override
    public SoloActionToken getExtractToken() {
        return null;
    }
}
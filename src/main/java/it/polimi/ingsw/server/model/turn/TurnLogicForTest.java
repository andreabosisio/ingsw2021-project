package it.polimi.ingsw.server.model.turn;

import it.polimi.ingsw.server.model.ModelInterface;
import it.polimi.ingsw.server.model.player.Player;

import java.util.List;

public class TurnLogicForTest extends TurnLogic{
    /**
     * constructor only used in testing
     *
     * @param players players in the game
     */
    public TurnLogicForTest(List<Player> players, ModelInterface modelInterface) {
        super(players, modelInterface);
    }

    /**
     * Set the next player and reset the current values.
     */
    @Override
    public void setNextPlayer() {
        if (isLastPlayerTurn())
            setCurrentPlayer(getPlayers().get(0));
        else
            setCurrentPlayer(getPlayers().get(getPlayers().indexOf(getCurrentPlayer()) + 1));
        reset();
    }
}

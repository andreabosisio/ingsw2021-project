package it.polimi.ingsw.server.events.receive;

import it.polimi.ingsw.server.exceptions.InvalidEventException;
import it.polimi.ingsw.server.model.ModelInterface;
import it.polimi.ingsw.server.utils.ServerParser;

/**
 * Represent the request of an End Turn Action by a Player (from the Client).
 */
public class EndTurnEventFromClient extends EventFromClient {

    /**
     * Create a new End Turn Action request by specifying the Player who wants to end the turn.
     *
     * @param nickname of the Player who wants to end the turn
     */
    public EndTurnEventFromClient(String nickname) {
        super(nickname, ServerParser.END_TURN_ACTION_TYPE);
    }

    /**
     * Executes the action.
     *
     * @param modelInterface The reference of the game model
     * @return true if the action has been correctly executed
     * @throws InvalidEventException        if it's an invalid action
     */
    @Override
    public boolean doAction(ModelInterface modelInterface) throws InvalidEventException {
        return modelInterface.endTurn();
    }
}
